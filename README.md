# Brazil JUG (Frictionless Java: Explorando o Quarkus, do Zero ao Deploy)

[![Frictionless Java](https://img.youtube.com/vi/oGZuz5vPtww/0.jpg)](https://www.youtube.com/watch?v=oGZuz5vPtww)

Este repositório contém o código fonte do workshop "Frictionless Java: Explorando o Quarkus, do Zero ao Deploy" apresentado no Brazil JUG.

## Criando um cluster kubernetes com KinD

```shell
kind create cluster --name braziljug
```

## Adicionando a infraestrutura necessária

### Adicionando o banco de dados PostgreSQL

```shell
helm install postgresql oci://registry-1.docker.io/bitnamicharts/postgresql --set global.postgresql.auth.database=braziljug --set global.postgresql.auth.postgresPassword=password
```

### Adicionando o LGTM

```shell
kubectl apply -f k8s/otel-lgtm.yaml
```

## Criando o projeto Quarkus

Criar o projeto com o Quarkus CLI:

```bash
quarkus create app org.acme:braziljug
```

Vamos ver o que foi criado, algumas extensões do Quarkus trazem pra gente uma funcionalidade que se chama codestarts, que são basicamente templates de código prontos para serem usados quando criamos a aplicação.

## Executando a aplicação

Vamos executar nossa aplicação e vamos verificar se a aplicação está funcionando corretamente.

```bash
mvn quarkus:dev
```

Acesse [http://localhost:8080/](http://localhost:8080/) e veja a mensagem "Hello from Quarkus REST".

## Adicionando uma nova extensão

Vamos adicionar três extensões ao nosso projeto: A primeira vai ser responsável pela parte de serialização e desserialização de JSON, a segunda vai ser a extensão do Hibernate ORM com Panache para facilitar o acesso ao banco de dados e a terceira vai ser a extensão do JDBC do MySQL.

Vamos agora escrever um endpoint simples que vai criar um novo `Movie` pra gente e vamos utilizar essa dependência.

Vamos criar uma entidade chamada `Movie` que vai representar a tabela de filmes no banco de dados. E pra isso a gente vai utilizar o PanacheEntity e vamos estender a nossa classe com ele. O PanacheEntity já traz pra gente o ID e vários métodos prontos para serem utilizados. Ele usa o padrão Active Record, mas você também pode utilizar o padrão Repository se preferir.

E o que é o Panache, o Panache é um wrapper em cima do Hibernate ORM que facilita bastante o nosso acesso ao banco de dados, ele traz uma série de métodos prontos para serem utilizados.

Beleza, agora vamos criar o nosso endpoint REST que vai permitir a criação de novos filmes no banco de dados.

Vamos testar, isso vai funcionar ou não, a gente não configurou a conexão com o banco de dados ainda. A gente nem tem o banco de dados rodando ainda.

Isso vai funcionar ou não? A resposta é sim, porque o Quarkus traz uma funcionalidade muito legal que é o Dev Services, que basicamente quando você está rodando a aplicação em modo de desenvolvimento, ele consegue identificar que você está utilizando uma extensão que precisa de um banco de dados e ele automaticamente sobe um container do banco de dados pra você, sem você precisar fazer nada.

## Adicionando observabilidade

Outra coisa interessante para uma aplicação que vai para produção é observabilidade, vamos adicionar a extensão `quarkus-micrometer-opentelemetry` para que a gente consiga expor as telemetrias da nossa aplicação.

## Empacotando nossa aplicação para produção

Vamos instalar algumas extensões:

```shell
mvn quarkus:add-extension -Dextensions="kubernetes,container-jib"
```

A extensão kubernetes vai ser responsável por gerar os manifests do Kubernetes para a nossa aplicação, enquanto a extensão container-jib vai ser responsável por empacotar a nossa aplicação em uma imagem Docker.

Mas antes a gente precisa configurar algumas coisas, precisamos configurar o nome da imagem final, a versão e o owner.

Além disso a gente precisa apontar a observabilidade, como vamos executar isso em um cluster local usando KinD, nós precisamos apontar o local do collector do Opentelemetry.

Além disso precisamos saber a senha do banco de dados e configurar aqui na aplicação, como eu disse o Quarkus é Kubernetes Native, então ele te ajuda e muito não somente na hora de executar sua aplicação em um cluster mas também na hora de configurar ela para isso.

## Buildando a aplicação

> [NOTE]
> Não esqueça de alterar a propriedade `quarkus.container-image.group` para o seu usuário do Docker Hub.

Se você quiser executar a aplicação no modo JVM, você pode fazer o build da aplicação com o seguinte comando:

```bash
mvn clean package
```

Se você quiser executar a aplicação no modo nativo, você pode fazer o build da aplicação com o seguinte comando:

```bash
mvn clean package -Pnative
```

## Subindo a imagem para o Docker Hub

```bash
docker push <your-docker-hub-username>/braziljug:0.1.0
```

## Deploy da aplicação no cluster KinD

Agora que a gente já tem a imagem da nossa aplicação criada, a gente pode fazer o deploy dela no nosso cluster KinD.

```bash
kubectl apply -f target/kubernetes
```

## Acessando a aplicação

Agora que a nossa aplicação está rodando no cluster, a gente pode acessar ela.

```bash
kubectl port-forward svc/braziljug 8080:8080
```

Acesse [http://localhost:8080/movies](http://localhost:8080/) e veja a lista de filmes (inicialmente vazia).

## Considerações finais

Parabéns! Você conseguiu criar uma aplicação Quarkus, adicionar funcionalidades a ela, empacotá-la em uma imagem Docker e fazer o deploy dela em um cluster Kubernetes local usando KinD. A partir daqui você pode explorar ainda mais o Quarkus e suas extensões para adicionar novas funcionalidades à sua aplicação.