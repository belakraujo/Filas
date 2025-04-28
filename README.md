# Simulador de Rede de Filas G/G/c/K

### **O que o código realiza**  
Este projeto implementa um **simulador de uma rede de filas G/G/c/K** em Java.  
Ele usa o SnakeYAML para ler configurações a partir de um arquivo `modelo.yml` e gera estatísticas sobre o funcionamento das filas: **clientes perdidos** e **distribuição de estados**.

**Três filas são simuladas:**
- **Fila 1** → G/G/1 → 1 servidor, capacidade máxima de 1 cliente.
- **Fila 2** → G/G/2/5 → 2 servidores, capacidade máxima de 5 clientes.
- **Fila 3** → G/G/2/10 → 2 servidores, capacidade máxima de 10 clientes.

A simulação inicia com um cliente chegando no tempo **2.0 minutos** e roda até aproximadamente **100.000 números aleatórios** serem utilizados.

> O tempo da simulação é exibido em minutos.

---

### **Arquivos Necessários**
- `SimuladorRedeFilas.java`
- `Fila.java`
- `Cliente.java`
- `Evento.java`
- `TipoEvento.java`
- `modelo.yml`
- `snakeyaml-1.33.jar`

---

### **Como Rodar o Código**  

1. **Organize os arquivos**:  
   Coloque todos os arquivos acima em uma mesma pasta (ex: `Filas/`).

2. **Compile o projeto**:  
   No terminal, acesse a pasta onde estão os arquivos e rode:
   
   - No **macOS**:
     ```bash
     javac -cp ".:snakeyaml-1.33.jar" *.java
     ```
   - No **Windows**:
     ```bash
     javac -cp ".;snakeyaml-1.33.jar" *.java
     ```

3. **Execute a simulação**:
   
   - No **macOS**:
     ```bash
     java -cp ".:snakeyaml-1.33.jar" SimuladorRedeFilas
     ```
   - No **Windows**:
     ```bash
     java -cp ".;snakeyaml-1.33.jar" SimuladorRedeFilas
     ```

---

### **Exemplo de Resultado Esperado**

Após a execução, o programa mostrará:
- O **tempo global da simulação** (em minutos).
- Estatísticas detalhadas de cada fila:
  - **Clientes perdidos**
  - **Distribuição de estados** (percentual de ocupação da fila ao longo da simulação).

---

### **Exemplo de modelo.yml**

```yaml
chegada:
  min: 2.0
  max: 4.0
  fila_inicial: 1

filas:
  1:
    servidores: 1
    capacidade: 1
    atendimento_min: 1.0
    atendimento_max: 2.0
    destinos:
      2: 0.8
      3: 0.2

  2:
    servidores: 2
    capacidade: 5
    atendimento_min: 4.0
    atendimento_max: 8.0
    destinos:
      2: 0.5
      3: 0.3
      saida: 0.2

  3:
    servidores: 2
    capacidade: 10
    atendimento_min: 5.0
    atendimento_max: 15.0
    destinos:
      3: 0.7
      saida: 0.3
