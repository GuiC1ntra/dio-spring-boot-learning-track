# Budgeting AI - Assistente financeiro por voz com Spring AI

Projeto do desafio da **DIO** (trilha Itaú - Java com Inteligência Artificial). É uma evolução do projeto base do expert ([05-spring-ai original](https://github.com/digitalinnovationone/dio-spring-boot-learning-track/blob/main/05-spring-ai/README.md)), mantendo a arquitetura em camadas (domain, application, infrastructure).

## O que o projeto faz

Uma API de orçamento que entende **comandos de voz** sobre gastos:

1. O cliente envia um áudio (ex.: "gastei 45 reais no mercado");
2. O áudio é transcrito para texto (Whisper);
3. A IA entende a intenção e chama uma ferramenta da aplicação (*Tool Calling*);
4. A transação é salva ou consultada no banco (MySQL);
5. A resposta final volta em **áudio** (Text-to-Speech).

## Melhorias que implementei

| Melhoria | Onde | Por quê |
|---|---|---|
| **Nova ferramenta de IA: resumo por categoria** | `SummarizeCategoryUseCase` | Agora dá para perguntar "quanto gastei no mercado?" e a IA responde com total e quantidade de transações |
| **Novo endpoint REST** `GET /transactions/{category}/summary` | `TransactionController` | O mesmo caso de uso serve REST e IA, sem duplicar regra |
| **Validação antes de salvar** | `Transaction` + `InvalidTransactionException` | Rejeita descrição vazia, valor <= 0 e categoria ausente (regra no domínio) |
| **Erros HTTP 400 padronizados** | `ApiExceptionHandler` | Retorna *ProblemDetail* (RFC 7807) em vez de erro 500 |
| **Correção na conversão de valores** | `TransactionOutput` | O valor é salvo em centavos, mas era exibido sem dividir por 100 (1250 aparecia como 1250.00 em vez de 12.50) |
| **Novas categorias** | `Category` | Adicionei FOOD, LEISURE e OTHER |
| **Prompt do assistente melhorado** | `prompts/system-message.st` | Explica que o valor é em centavos, pede dados faltantes e responde curto (vira áudio) |
| **Testes unitários** | `src/test` | Cobrem validação, conversão de valor e resumo por categoria, sem depender de banco nem da OpenAI |

## Tecnologias

Java, Spring Boot, Spring AI (OpenAI: chat, Whisper e TTS), Spring Data JPA, MySQL (Docker Compose), Gradle, JUnit 5 e AssertJ.
## Como executar

Requisitos: JDK conforme o `build.gradle`, Docker e uma chave da OpenAI.

```bash
cd 05-spring-ai
export OPENAI_API_KEY=sua-chave-aqui
./gradlew bootRun
```

O Spring Boot sobe o MySQL automaticamente pelo `compose.yml` (spring-boot-docker-compose).

## Como testar o fluxo principal

**1. Testes unitários** (não precisam de chave nem de banco):

```bash
./gradlew test --tests "dio.budgeting.domain.*" --tests "dio.budgeting.application.*"
```

**2. Criar uma transação via REST** (valor em centavos):

```bash
curl -X POST http://localhost:8080/transactions -H "Content-Type: application/json" -d '{"description":"Mercado","category":"GROCERIES","amount":4500}'
```

**3. Ver o resumo por categoria** (nova melhoria):

```bash
curl http://localhost:8080/transactions/GROCERIES/summary
```

Exemplo de resposta:

```json
{ "category": "GROCERIES", "transactionCount": 1, "total": 45.0 }
```

**4. Testar a validação** (valor inválido retorna 400):

```bash
curl -X POST http://localhost:8080/transactions -H "Content-Type: application/json" -d '{"description":"Teste","category":"AUTO","amount":0}'
```

**5. Fluxo completo por voz** (precisa da chave OpenAI):

```bash
curl -X POST http://localhost:8080/transactions/ai -F "file=@src/test/resources/audio/recording-1.m4a" --output resposta.mp3
```

Peça no áudio algo como *"quanto eu gastei no mercado?"* para acionar a nova ferramenta de resumo.

## Estrutura

```
dio.budgeting
  domain          -> Transaction, Category, regras e contrato do repositório
  application     -> casos de uso (usados pelo REST e como @Tool pela IA)
  infrastructure  -> controller HTTP, JPA e adaptadores
```

## O que aprendi

- Como o **Tool Calling** transforma casos de uso comuns (métodos com `@Tool`) em ações que a IA consegue executar.
- Que reaproveitar o mesmo caso de uso no REST e na IA evita regra duplicada e mantém a arquitetura limpa.
- Que a **validação precisa ficar no domínio**, porque a IA também pode mandar dados errados.
- Como o prompt do sistema influencia o comportamento (por exemplo, pedir respostas curtas porque viram áudio).
- A importância de testes que não dependem de serviços externos, para não gastar créditos da OpenAI.

## Observações

- Os testes de integração do projeto base (*IT*) só rodam com `OPENAI_API_KEY` definida.
- Os testes unitários novos foram escritos sem depender de Spring, banco ou OpenAI.

## Autor

**Guilherme Cintra** - [LinkedIn](https://linkedin.com/in/guilherme-cintr4) | [GitHub](https://github.com/GuiC1ntra)

Projeto base: [digitalinnovationone/dio-spring-boot-learning-track](https://github.com/digitalinnovationone/dio-spring-boot-learning-track).
