package org.isihop.fr.shellClient;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;


@ShellComponent(value = "Mes commandes de messagerie")
public class MyShellCommand {


//cette ligne ici pour demo qu'il est possible de mettre en place 
// une value dynamique.
//@Value("${application.topicin}")
//private String TOPICIN;
    
//spring.kafka.consumer.group-id
@Value("${spring.kafka.consumer.group-id}")
private String groupid;
    
//instanciation du service Kafka
@Autowired
private KafkaTemplate<String, String> kafkaTemplate;

@Value("${application.topicout}")
private String TOPICOUT;  //ecriture par le client

@Value("${application.topictechout}")
private String TOPICTECHOUT; //ecriture tech par le client

@Value("${application.monnom}")
private String MONNOM;

@Value("${spring.kafka.producer.bootstrap-servers}")
private String connection;

//login & sout
private static final Logger logger = LoggerFactory.getLogger(MyShellCommand.class);

    public MyShellCommand() {
        //int ale=(int)(Math.random()*99998)+1;
        //groupid=groupid+"_"+ale;
        
        //logger.error("Le grouId de ce client est : "+ groupid);
    }


    /******************************
     * whoami, donne le nom du client
     ******************************/
    @ShellMethod(value="Quel est mon nom et ma connexion ?", group="Messagerie")
    public void whoami() {
            logger.error(String.format("#### -> Votre nom de login est : "+MONNOM));   
            logger.error("Vous êtes connecté sur kafka vers : "+ connection);
    }


    /******************************
     * lister les var de l'env sys
     ******************************/
    @ShellMethod(value="Lister les variables de l'environnement système...", group="Operations")
    public void env() {
            logger.error("===============Environnement===============");
            Map<String, String> variablesEnv = System.getenv();

            // Affiche chaque variable et sa valeur
            for (Map.Entry<String, String> entry : variablesEnv.entrySet()) {
                logger.error(entry.getKey() + " = " + entry.getValue());
            }
            logger.error("===============Environnement===============");
    }


    /******************************
     * Envoyer un msg vers un client
     * courant out
     * @param msg 
     * @param dst 
     ******************************/
    @ShellMethod(value="Envoyer un message au broker Kafka vers un destinataire.", group="Messagerie")
    public void message(@ShellOption(help = "Votre message") String msg,@ShellOption(help = "Nom du client destinataire(attention à la casse!)") String dst) {
        //logger.error(String.format("#### -> %s: Envoi du message -> %s", MONNOM,dst+":"+msg));
        
        try
        {
            //format du message FROM:ClientX#TO:ClientY#message
            this.kafkaTemplate.send(TOPICOUT,"FROM:"+MONNOM+"#TO:"+dst+"#"+msg);
        } 
        catch (Exception e) 
        {
            logger.error(String.format("#### -> Erreur d'envoi du message vers le topic %s par defaut -> %s", TOPICOUT,dst+":"+msg));      
        }
    }
    
       
    /******************************
     * Traduire un message
     * ie s'envoyer un message <=> a traduire le msg
     * @param txt 
     ******************************/
    @ShellMethod(value="Demander de traduire un bout de texte.", group="Messagerie")
    public void traduire(@ShellOption(help = "Votre texte à traduire") String txt) {
        //logger.error(String.format("#### -> %s: Traduction de  -> %s", MONNOM,":"+txt));
        
        try
        {
            //format du message FROM:ClientX#TO:ClientX#message
            //j'envoi un message a moi mê
            this.kafkaTemplate.send(TOPICOUT,"FROM:"+MONNOM+"#TO:"+MONNOM+"#"+txt);
        } 
        catch (Exception e) 
        {
            logger.error(String.format("#### -> Erreur d'envoi de traduction vers le topic %s par defaut -> %s", TOPICOUT,":"+txt));      
        }
    }
    
    
    /*********************************
     * Listener Kafka de la console
     * recupére les messages afin de les
     * afficher à l'écran
     * @param message 
     *********************************/
    @KafkaListener(topics = "${application.topicin}")
    //@KafkaListener(topics = "${application.topicin}",groupId ="#{__listener.groupid}")
    //@KafkaListener(topics="#{__listener.TOPICIN}")
    public void consume(String message) {

        if (extract_TO(message).compareToIgnoreCase(MONNOM.trim())==0) //si c'est bien pour moi !
        {
            logger.error(String.format("#### -> De la part de %s : Message -> %s",extract_FROM(message), extract_MSG(message)));
        }
    }
    
    /*********************************
     * Listener TECH Kafka de la console
     * @param messageTech 
     *********************************/
    @KafkaListener(topics = "${application.topictechin}")
    public void consumeTech(String messageTech) {
        if (extract_TO(messageTech).compareToIgnoreCase(MONNOM.trim())==0) //si c'est bien pour moi!
        {
            logger.error(String.format("#### -> %s: Reception du message Technique de %s -> %s",MONNOM, extract_FROM(messageTech),extract_MSG(messageTech)));
        }
    }
    
    
    /******************************
     * Lister les clients connecté
     * courant out 
     ******************************/
    @ShellMethod(value="Lister les clients connectés a la messagerie.", group="Messagerie")
    public void listerClients() {
        //envoyer le message tech GET:ClientX
        String msg="GET:"+MONNOM.trim();
        //logger.error(String.format("#### -> %s: Envoi du message -> %s", MONNOM,msg));
        
        try
        {
        this.kafkaTemplate.send(TOPICTECHOUT, msg);
        } 
        catch (Exception e) 
        {
            logger.error(String.format("#### -> Erreur d'envoi du message vers le topic %s par defaut -> %s", TOPICTECHOUT,msg));      
        }
    }

    
    /******************************
     * Enregistrer le client connecté
     * courant out 
     ******************************/
    @ShellMethod(value="Enregistrer le client connecté a la messagerie.", group="Operations")
    public void EnregistrerClient() {
        String msg="CONNECT:"+MONNOM.trim();
        //logger.error(String.format("#### -> %s: Envoi du message -> %s", MONNOM,msg));
        
        try
        {
        this.kafkaTemplate.send(TOPICTECHOUT, msg);
        } 
        catch (Exception e) 
        {
            logger.error(String.format("#### -> Erreur d'envoi du message vers le topic %s par defaut -> %s", TOPICTECHOUT,msg));      
        }
    }
    
    /******************************
     * Déconnecter le client 
     * courant out 
     ******************************/
    @ShellMethod(value="Déconnecter le client de la messagerie sans quitter.", group="Operations")
    public void DeconnecterClient() {
        String msg="DISCONNECT:"+MONNOM.trim();
        //logger.error(String.format("#### -> %s: Envoi du message -> %s", MONNOM,msg));
        
        try
        {
        this.kafkaTemplate.send(TOPICTECHOUT, msg);
        } 
        catch (Exception e) 
        {
            logger.error(String.format("#### -> Erreur d'envoi du message vers le topic %s par defaut -> %s", TOPICTECHOUT,msg));      
        }
    }


    /******************************
     * Tester la connexion du client 
     * @param client
     ******************************/
    @ShellMethod(value="Tester la connexion du client.", group="Operations")
    public void isConnected(@ShellOption(help = "Nom du client a tester") String client) {
        String msg="ISCONNECTED:"+MONNOM.trim()+"#"+client.trim(); //ISCONNECTED:FROM#WHO
        //logger.error(String.format("#### -> %s: Envoi du message -> %s", MONNOM,msg));
       
        try
        {
        this.kafkaTemplate.send(TOPICTECHOUT, msg);
        } 
        catch (Exception e) 
        {
            logger.error(String.format("#### -> Erreur d'envoi du message vers le topic %s par defaut -> %s", TOPICTECHOUT,msg));      
        }
    }

    
    /*********************************
     * Quitter le shell et deconnecter 
     * le client
     */
    @ShellMethod(value="Quitter le shell et deconnecter le client", group="Messagerie")
    public void byebye() {
        String msg="DISCONNECT:"+MONNOM.trim();
        //logger.error(String.format("#### -> %s: Envoi du message -> %s", MONNOM,msg));
        
        try
        {
        this.kafkaTemplate.send(TOPICTECHOUT, msg);
        } 
        catch (Exception e) 
        {
            logger.error(String.format("#### -> Erreur d'envoi du message vers le topic %s par defaut -> %s", TOPICTECHOUT,msg));      
        }
        //quitter
        System.exit(0);
    }

    
    /********************************
     * Extraire le destinataire du
     * message
     * @param message
     * @return 
     ********************************/
    private String extract_TO(String message) 
    {
         //format du message FROM:ClientX#TO:ClientY#message
        String [] segments=message.split("#");
        return segments[1].substring(3);
    }
    
      /********************************
     * Extraire l'expediteur du
     * message
     * @param message
     * @return 
     ********************************/
    private String extract_FROM(String message) 
    {
        String [] segments=message.split("#");
        return segments[0].substring(5);
    }
    
      /********************************
     * Extraire le message du
     * message
     * @param message
     * @return 
     ********************************/
    private String extract_MSG(String message) 
    {
        String [] segments=message.split("#");
        return segments[2];
    }
   
}
