package org.isihop.fr.shellClient;

/**
 *
 * @author tondeur-h
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class StartupAction implements SmartLifecycle {

private static final Logger logger = LoggerFactory.getLogger(StartupAction.class);
    
private boolean running = false;
    
@Autowired
private KafkaTemplate<String, String> kafkaTemplate;

@Value("${application.topictechout}")
private String TOPICTECHOUT;

@Value("${application.monnom}")
private String MONNOM;

@Value("${spring.kafka.producer.bootstrap-servers}")
private String connection;

    /************************************
     * Se connecter a Kafka au démarrage
     ************************************/
    @Override
    public void start() {
        logger.error("Enregistrement du client");
    
        //envoyer le message CONNECT:NomCli
        String msg="CONNECT:"+MONNOM.trim();
        logger.error(String.format("#### -> %s: Envoi du message -> %s", MONNOM,msg));
        logger.error("Attente Kafka, 60 secondes.");
        try
        {
            this.kafkaTemplate.send(TOPICTECHOUT, msg);
        } 
        catch (Exception e) 
        {
            logger.error(String.format("#### -> Erreur d'envoi du message vers le topic %s -> %s, Kafka n'est pas a l'écoute sur %s !", TOPICTECHOUT,msg,connection));
            logger.error("Exception: "+e.getMessage()+" : Ctrl+Z to stop.");
            System.exit(0);
        }

        //recuperer la liste des clients connecté.
        msg="GET:"+MONNOM.trim();
        logger.error(String.format("#### -> %s: Envoi du message -> %s", MONNOM,msg));
        logger.error("Attente Kafka, 60 secondes.");
        
        try
        {
            this.kafkaTemplate.send(TOPICTECHOUT, msg);
        } 
        catch (Exception e) 
        {
            logger.error(String.format("#### -> Erreur d'envoi du message vers le topic %s -> %s, Kafka n'est pas a l'écoute sur %s !", TOPICTECHOUT,msg,connection)); 
            logger.error("Exception: "+e.getMessage()+" : Ctrl+Z to stop.");
            System.exit(0);
        }
        
        //ok tout va bien!
        running = true;
    }

    /************************************
     * Message à la fermeture pour verif.
     ************************************/
    @Override
    public void stop() {
        //juste pour info.
        logger.error("Fermeture du client "+MONNOM);
        running = false;
    }

    /**********************************
     * Override isRunning
     * @return 
     **********************************/
    @Override
    public boolean isRunning() {
        return running;
    }
}
