# AGENTS.md — OpenPMO API

> Contexto técnico para agentes de IA que trabalham neste repositório. Este arquivo foi elaborado a partir do `build.gradle`, `gradle.properties`, `gradle/wrapper/gradle-wrapper.properties`, `src/main/resources/application.properties`, `Dockerfile` e da estrutura atual de `src`.

## 📚 Fonte de Conhecimento & Regras de Negócio (OpenPMO)

Para entender as regras de negócio, arquitetura, fluxos de usuário e especificações do projeto, consulte a documentação oficial via **GitBook LLM Index**:

- **Índice Geral de IA (`llms.txt`):** `https://sep-es-br.gitbook.io/manuais-openpmo/llms.txt`
- **Manual Principal:** `https://sep-es-br.gitbook.io/manuais-openpmo`

### 💡 Como o Codex deve consultar a documentação:
1. Sempre que precisar entender um fluxo de negócio, autenticação ou funcionalidade (ex: *Conceitos Básicos*, *Acompanhamento de Projetos*), consulte primeiro os links mapeados no `llms.txt`.
2. Para obter a documentação em formato Markdown limpo de qualquer página, adicione a extensão `.md` ao final da URL da página correspondente.
   - *Exemplo:* Se a página for `https://sep-es-br.gitbook.io/manuais-openpmo/manual-do-usuario/conceitos-basicos`, acesse `https://sep-es-br.gitbook.io/manuais-openpmo/manual-do-usuario/conceitos-basicos.md`.

O GitBook é a **fonte da verdade para regras de negócio, fluxos funcionais e comportamento esperado do OpenPMO**. Antes de implementar ou corrigir uma funcionalidade, consulte essa documentação e alinhe a mudança ao que estiver especificado nela. Use o código e a configuração locais como fonte da verdade para o estado técnico atualmente implementado; se houver conflito, não invente uma regra: registre a divergência e confirme qual comportamento deve prevalecer.

## Perfil técnico

- **Linguagem principal:** Java 11. O `build.gradle` define `sourceCompatibility = '11'` e o `gradle.properties` indica JDK 11.
- **Build:** Gradle Wrapper 7.3.1 (`gradle/wrapper/gradle-wrapper.properties`). O projeto raiz Gradle chama-se `open-pmo`.
- **Framework principal:** Spring Boot `2.2.12.RELEASE`.
- **Gerenciamento de dependências:** Spring Dependency Management `1.0.8.RELEASE`.
- **Plugins de build:** Java, SonarQube `2.7` e JaCoCo.
- **Empacotamento:** aplicação Spring Boot executável; o artefato usado pelo `Dockerfile` é `build/libs/app.jar`.
- **Persistência:** Neo4j via Spring Data Neo4j e driver Bolt; repositórios são habilitados para `br.gov.es.openpmo.repository`.
- **Execução local configurada:** contexto HTTP `/openpmo`, porta `8080`, Neo4j local em `bolt://localhost:7687` e fuso padrão `GMT-3:00`.

O `README.md` e o `Dockerfile` ainda contêm referências a Java 8, mas a configuração efetiva de compilação do projeto é Java 11. Não rebaixe a linguagem nem altere a imagem de runtime sem uma tarefa específica para compatibilização da implantação.

## Dependências principais

As versões abaixo são as versões declaradas diretamente no `build.gradle`. Os starters Spring sem número explícito recebem a versão administrada pelo BOM do Spring Boot `2.2.12.RELEASE`; preserve esse mecanismo ao adicionar ou atualizar dependências.

### Spring, web e segurança

- `org.springframework.boot:spring-boot-starter-oauth2-client` — `2.2.12.RELEASE` via Spring Boot.
- `org.springframework.boot:spring-boot-starter-webflux` — `2.2.12.RELEASE` via Spring Boot.
- `org.springframework.boot:spring-boot-starter-security` — `2.2.12.RELEASE` via Spring Boot.
- `org.springframework.boot:spring-boot-starter-web` — `2.2.12.RELEASE` via Spring Boot.
- `org.springframework.boot:spring-boot-starter-validation` — `2.2.12.RELEASE` via Spring Boot.
- `org.springframework.boot:spring-boot-starter-aop` — `2.2.12.RELEASE` via Spring Boot.
- `org.springframework.boot:spring-boot-starter-data-neo4j` — `2.2.12.RELEASE` via Spring Boot.
- `org.springframework.boot:spring-boot-starter-mail` — `2.2.12.RELEASE` via Spring Boot.
- `org.springframework.data:spring-data-neo4j` — `5.2.13.RELEASE`.
- `org.neo4j.driver:neo4j-java-driver` — `4.4.12`.
- `org.neo4j:neo4j-jdbc-bolt` — `4.0.1`.
- `io.jsonwebtoken:jjwt` — `0.7.0`.
- `javax.xml.bind:jaxb-api` — `2.3.1`.
- `org.zalando:logbook-spring-boot-starter` — `2.14.0`.

### Utilidades, serialização e relatórios

- `org.modelmapper:modelmapper` — `2.3.6`.
- `org.apache.commons:commons-text` — `1.10.0`.
- `org.apache.commons:commons-lang3` — `3.9`.
- `org.apache.httpcomponents:httpclient` — `4.5.12`.
- `org.json:json` — `20190722`.
- `com.fasterxml.jackson.datatype:jackson-datatype-jsr310` — versão administrada pelo Spring Boot `2.2.12.RELEASE`.
- `com.google.code.gson:gson` — `2.8.9`.
- `net.sf.jasperreports:jasperreports` — `6.17.0`.
- `com.lowagie:itext` — `2.1.7`.

### API e documentação

- `io.springfox:springfox-swagger2` — `2.9.2`.
- `io.springfox:springfox-swagger-ui` — `2.9.2`.
- `io.springfox:springfox-bean-validators` — `2.9.2`.
- Testes: `org.springframework.boot:spring-boot-starter-test` — `2.2.12.RELEASE` via Spring Boot.

### Interfaces e plugins OpenPMO

- `com.github.sep-es-br:pmo-core-identity-parser` — `1.0.6`.
- `com.github.sep-es-br:pmo-core-organization-parser` — `1.1.2`.
- `com.github.sep-es-br:openpmo-plugin-agreement-interface` — `1.0.6`.
- `com.github.sep-es-br:openpmo-plugin-procurement-interface` — `1.0.0`.
- `com.github.sep-es-br:openpmo-plugin-obligation-interface` — `1.0.0`.
- `com.github.sep-es-br:openpmo-plugin-administrative-process-interface` — `1.0.0`.
- `com.github.sep-es-br:openpmo-plugin-user-a-identify-goves` — `v2.2.0`.
- `com.github.sep-es-br:openpmo-plugin-sigef-interface` — `2.0.0`.

Os parsers e plugins adicionais podem ser injetados pelas propriedades `app.parser.repository`, `app.organization.parser.repository`, `app.agreement.parser.repository`, `app.procurement.parser.repository`, `app.obligation.parser.repository`, `app.edocs.plugin.repository`, `app.plugin.user.a.identify.parse.repository` e `app.plugin.sigef.repository`. O `build.gradle` lê essas coordenadas do `application.properties` e só adiciona as opcionais quando preenchidas. Preserve esse mecanismo de configuração em vez de duplicar coordenadas no script de build.

## Estrutura de pastas e convenções

O código Java está no pacote-base `br.gov.es.openpmo`:

- `src/main/java/br/gov/es/openpmo/OpenPmoApplication.java` — bootstrap Spring Boot; habilita cache, agendamento, varredura de propriedades e repositórios Neo4j.
- `controller/` — controllers REST, organizados por domínio (`workpack`, `plan`, `actor`, `schedule`, `reports`, `plugins`, `filters`, entre outros).
- `service/` — regras de aplicação e casos de uso, também organizados por domínio; subpastas importantes incluem `workpack`, `schedule`, `risk`, `stakeholder`, `organization`, `properties`, `reports` e `ui`.
- `repository/` — acesso e consultas ao Neo4j.
- `model/` — modelos persistidos e objetos de domínio; modelos de propriedades incluem `BudgetPlanSelection`, `FinancialSourceSelection` e outros tipos de propriedade.
- `dto/` — contratos de entrada e saída da API, incluindo DTOs específicos de workpack e propriedades.
- `configuration/` — configuração Spring, propriedades e integrações transversais.
- `apis/` — clientes e modelos de APIs externas, incluindo Acesso Cidadão.
- `scheduler/` — tarefas agendadas por expressões cron configuradas em `application.properties`.
- `enumerator/`, `exception/` e `utils/` — enums, tratamento global de exceções e utilitários compartilhados.
- `src/main/resources/application.properties` — configuração local e de integração; `messages.properties` — mensagens; `doc_swagger.json` e `doc_swagger.yaml` — documentação da API.
- `src/test/` — testes unitários/de serviço com Spring Boot Test e JUnit gerenciado pelo starter. Não há subprojeto Gradle separado.

Convenções de implementação:

- Adicione endpoints em controllers por domínio e mantenha a lógica de negócio nos services; não mova regras para o controller apenas por conveniência.
- Preserve os contratos REST e os envelopes de resposta existentes (`success`, `data`, `pagination` e `message`) ao alterar endpoints.
- Para persistência, reutilize repositories, models e padrões de consulta Neo4j existentes. Mudanças de modelo devem considerar dados legados, consultas de leitura e recarga/atualização de valores relacionados.
- O SIGEF é opcional via `Optional<ISigefProvider>`. A disponibilidade é exposta por `/plugins/availability`; quando o plugin está ausente, o fluxo deve continuar funcionando e dados legados não devem ser descartados por inferência.
- Autenticação e autorização usam Spring Security/OAuth2, integração com provedor OIDC e JWT. Consulte o GitBook antes de alterar fluxos de login, permissões ou identidade.
- O frontend consome a API sob `/openpmo`; ao alterar caminhos ou cookies, verifique também o proxy, `app.homeURI`, CORS e o comportamento do frontend.
- Arquivos, relatórios, imagens e journals usam caminhos configuráveis (`app.pathImagens`, `app.reportPath` e `app.journalPath`). Não fixe caminhos de máquina em novas funcionalidades.
- Não coloque credenciais OAuth, JWT, e-mail, banco, Pentaho ou APIs externas em documentação, commits ou logs. Os valores existentes em arquivos locais devem ser tratados como configuração sensível e externalizados em ambientes reais.

## Comandos principais

Execute os comandos a partir da raiz do repositório. No Windows, use o wrapper `gradlew.bat` e JDK 11.

```powershell
# Compilar, testar e empacotar
.\gradlew.bat clean build

# Rodar apenas os testes
.\gradlew.bat test

# Verificações do ciclo Gradle (inclui testes e demais checks configurados)
.\gradlew.bat check

# Executar a aplicação localmente
.\gradlew.bat bootRun

# Gerar o bootable JAR
.\gradlew.bat clean bootJar
```

Quando o cache Gradle do ambiente puder conflitar com outro processo, use um cache fora do repositório e desabilite o daemon, por exemplo `.\gradlew.bat --no-daemon --gradle-user-home <cache-fora-do-repositorio> clean build`. O erro `Unable to establish loopback connection` em uma execução com JDK incompatível ou cache bloqueado é falha do ambiente; não o trate como evidência de falha do código sem repetir com JDK 11 e cache isolado.

Não existe um script de lint dedicado no `build.gradle`. A qualidade está integrada aos plugins SonarQube `2.7` e JaCoCo; execute as tarefas correspondentes somente quando o ambiente tiver o servidor e as credenciais configurados, sem registrar tokens nos artefatos.

## Validação antes de concluir uma mudança

1. Consulte o `llms.txt`/GitBook para o fluxo de negócio afetado.
2. Confirme os contratos no controller, DTO, service, repository e configuração relacionados.
3. Execute o teste mais próximo da mudança e, para alterações transversais, `clean build`.
4. Verifique mudanças em endpoints, autenticação, cookies, arquivos, agendamentos e integrações opcionais em conjunto com o frontend quando aplicável.
5. Separe falhas de ambiente (JDK, Gradle, cache, Neo4j ou serviços externos) de falhas de compilação/teste do código e informe exatamente o que foi validado.
