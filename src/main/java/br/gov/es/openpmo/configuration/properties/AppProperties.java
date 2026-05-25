package br.gov.es.openpmo.configuration.properties;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "app")
public class AppProperties {

  private final Double searchCutOffScore;
  
  private final Login login;
          
     public static class Login {
         private Map<String, String> name;
         
         
        public String getName(String registrationId) {
            return name.get(registrationId);
        }

        public Map<String, String> getName() {
            return name;
        }

        public void setName(Map<String, String> name) {
            this.name = name;
        }
         
         
     }
     
   public String serverNameByRegistrationId(String registrationId){
       return login.getName(registrationId);
   }
  
  public AppProperties(final Double searchCutOffScore, final Login login) {
    this.searchCutOffScore = searchCutOffScore;
    this.login = login;
  }

  public Double getSearchCutOffScore() {
    return this.searchCutOffScore;
  }

}
