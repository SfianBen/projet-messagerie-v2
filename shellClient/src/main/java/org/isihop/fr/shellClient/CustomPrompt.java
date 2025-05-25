package org.isihop.fr.shellClient;

/**
 *
 * @author tondeur-h
 */

import org.springframework.stereotype.Component;
import org.jline.utils.AttributedString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.jline.PromptProvider;

@Component
public class CustomPrompt implements PromptProvider {

    //recuperer le nom du client 
@Value("${spring.kafka.consumer.group-id}")
private String NOMAPPLI; 

//recupere le nom de l'application
@Value("${application.monnom}")
private String MONNOM;

    
@Override
public AttributedString getPrompt() {
    //prompt qui se affiché à la manière de linux
    return new AttributedString(MONNOM+"@"+NOMAPPLI+" $> ");
}

}
