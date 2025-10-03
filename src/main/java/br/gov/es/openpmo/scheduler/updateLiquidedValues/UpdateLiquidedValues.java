/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.scheduler.updateLiquidedValues;

import br.gov.es.openpmo.utils.RestTemplateUtils;
import com.fasterxml.jackson.databind.JsonNode;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author gean.carneiro
 */
@Component
public class UpdateLiquidedValues {
    
    
    public final Logger LOGGER = Logger.getLogger(UpdateLiquidedValues.class.getSimpleName());
    
    @Value("${pentaho.api.liquidacao.url}")
    private String liquidacaoUrl;
        
    @Value("${pentahoBI.userId}")
    private String pentahoUserId;

    @Value("${pentahoBI.password}")
    private String pentahoPassword;
    
    
    private final RestTemplateUtils restTemplateUtils = new RestTemplateUtils();
  
    @Transactional
    public void updatedLiquidedValues(){
        
        

      
    }
    
    /**
     * 
     * @param codUo
     * @param codPo
     * @param codContrato
     * @param codConvenio
     * @param categoria
     * @param fonte
     * @return
     * @throws Exception 
     */
    
    public JsonNode fetchFromPentaho(String codUo, String codPo, String codContrato, String codConvenio, String categoria, String fonte) throws Exception {
        RestTemplate restTemplate = restTemplateUtils.createRestTemplateWithNoSSL();
    
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        String url = String.format(this.liquidacaoUrl, codUo, codPo, codContrato, codConvenio, categoria, fonte);
        
        

        CompletableFuture<JsonNode> futureResponse = restTemplateUtils.createRequestWithAuth(restTemplate,
                url,
                pentahoUserId,
                pentahoPassword
        );
        JsonNode response = futureResponse.join();

        return response;
    }
}
