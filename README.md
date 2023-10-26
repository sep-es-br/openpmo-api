# openpmo-api

O produto é composto por 3 componentes:

* Frontend Angular, montado a partir do repositório no github
* Backend (Java 8, Spring Boot 2.2.12.RELEASE), montado com Gradle a partir do repositório no github
* Banco de dados Neo4j, versão 4.4.19

Os componentes podem ser hospedados em um único servidor ou em servidores distintos.

O servidor que hospedar o Frontend requer a instalação e configuração de um servidor web http, como Apache ou Nginx.

Após o build do Frontend, o conteúdo da pasta "*dist*" deve ser copiado para a pasta de consumo (root directory) do servidor http.

Ainda no servidor http, deve ser criado um proxy para desviar as requisições direcionadas ao diretório virtual `/openpmo/` do servidor do
frontend para o diretório virtual `/openpmo/` do servidor do backend na porta 8080.

Assim: `<servidor do frontend>/openpmo/` para `<servidor do backend (api)>:8080/openpmo/`

O(s) servidor(es) que hospedar(em) o banco de dados e/ou o backend requerem a instalação do *Java JRE versão 8*.

A instalação do OpenPMO é configurável via arquivo de configuração para suportar qualquer sistema de autenticação padrão OAuth 2.0 OpenId Connect.

1. Twitter não é suportado por não usar `OAuth 2.0`.
2. São suportados sistemas `OAuth 2.0` compatíveis com *Google*, como *Acesso Cidadão*, *Facebook* e *Linkedin*.

Os usuários administradores do OpenPMO serão identificados em tempo de instalação via arquivo de configuração, que listará os usuários do sistema
de autenticação adotado que terão perfil *Administrator* naquela instalação do OpenPMO.

Toda pessoa que se logar no OpenPMO e que não for "*Administrator*" terá o perfil "*User*" no OpenPMO.

O Open PMO não deve permitir a edição de campos informados pelo Serviço de Autenticação, mas deve manter tais informações em sua base de dados, para
efeito de histórico e consultas posteriores diretamente ao banco de dados.

### Instalação

A API do OpenPMO espera que já exista o nó do serviço de autenticação criado no banco de dados Neo4j, para isso crie o nó utilizado o comando abaixo:

```cypher

CREATE (authService:AuthService {
  server:   'nome-do-servico-de-autenticacao',
  endpoint: 'endpoint-de-logout-do-servico-de-autenticacao'
})
```

Após isso, será necessário adicionar as configurações obrigatórias do arquivo `application.properties` localizado em `src/main/resources/`. Abaixo
segue a explicação e exemplo de cada propriedade necessária para o correto funcionamento da API.

- `app.default-timezone`
  - Define o timezone da aplicação, necessário para qualquer operação que envolva datas.
- `app.version`
  - Define a versão atual da aplicação, essa versão irá aparecer no frontend.
  - Exemplo: `app.version=1.0.0`
- `app.login.server.name`
  - Define o nome do serviço de autenticação. Deve possuir o mesmo nome do nó inicial adicionado manualmente.
  - Exemplo: `app.login.server.name=AcessoCidadao`
- `app.pathImagens`
  - Define o diretório do servidor onde será armazenado as imagens. Esse diretório deve ser criado antes de iniciar a aplicação e deve possuir e o
    OpenPMO deve possuir permissões leitura e escrita no diretório.
  - Exemplo: `app.pathImagens=/tmp/open-pmo/imagens/`
- `app.journalPath`
  - Define o diretório do servidor onde será armazenado arquivos relacionados ao `journal`. Preferencialmente deve-se coloca-lo no mesmo diretório
    base das imagens.
  - Exemplo: `app.journalPath=/tmp/open-pmo/journal/`
- `app.scheduler.*`
  - Essas propriedades definem constantes de tempo onde uma tarefa deve ser iniciada pelo `scheduler` do Spring Boot. Não é recomendado
    alteração exceto se o código-fonte esteja a ser alterado em conjunto.
  - Constantes:
    ```properties
      app.scheduler.everyday-at-0pm=0 0 0 1/1 * ?
      app.scheduler.everyday-every-1min=0 0/1 * 1/1 * ?
      app.scheduler.everyday-every-1hour=0 0 0/1 1/1 * ?
    ```
- `server.servlet.context-path`
  - Define a rota inicial da API, não deve ser alterado exceto se necessário para alterações no código-fonte do OpenPMO.
- `spring.data.neo4j.*`
  - Define propriedades de conexão com o banco de dados Neo4j.
- `spring.security.oauth2.client.*`
  - Define propriedades de configuração do servidor de autenticação OAuth2.
- `jwt.*`
  - Define propriedades de configuração do `JWT`.
- `server.ssl.*`
  - Define propriedades de configuração do `SSL`.
- `users.administrators`
  - Define o email dos administradores iniciais separado por vírgula, necessário para quando a aplicação for executada pela primeira vez por não
    haver usuários.
    pré-definidos.
  - Exemplo: `users.administrators=usuario_um@gmail.com,usuario_dois@gmail.com`
- `api.acessocidadao.*`
  - Define propriedades de configuração para chamadas da API do Acesso Cidadão.
- `api.e-docs.*`
  - Define propriedades de configuração para chamadas da API do E-Docs.
- `api.organograma.uri.webapi`
  - Define url para chamada de API do organograma.
- `org.neo4j.driver.pool.max-connection-pool-size`
  - Define o tamanho máximo da pool de conexões com o banco de dados Neo4j.

- Para desabilitar os logs consultas do Neo4j deve-se adicionar as seguintes propriedades ao `application.properties`

```properties
logging.level.org.neo4j.ogm.drivers.bolt.request.BoltRequest=OFF
logging.level.org.neo4j.ogm.context.GraphEntityMapper=OFF
logging.level.org.neo4j.ogm.drivers.bolt.response.BoltResponse=OFF
logging.level.org.neo4j.ogm.metadata.reflect.EntityAccessManager=OFF
logging.level.org.neo4j.ogm.context.SingleUseEntityMapper=OFF
```
