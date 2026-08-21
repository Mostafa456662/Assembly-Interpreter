# Assembly Interpreter in Java ⚙️

A lightweight, robust, modular Assembly Language Interpreter built in Java. It parses, validates, resolves labels, and executes custom assembly instructions in a simulated CPU runtime environment featuring registers, memory, and status flags.

---

## 🚀 Key Features

- **3-Phase Architecture**: Clean separation between **Tokenizer** (lexical analysis & syntax validation), **Parser** (label resolution & program counter mapping), and **Executor** (CPU runtime execution).
- **Simulated Hardware State**:
  - **8 Registers**: `R0` through `R7`.
  - **Memory Space**: 256 addressable integer memory slots (`0` to `255`).
  - **ALU Flags**: `zeroFlag` (Z) and `negativeFlag` (N) for conditional branching.
- **Robust Exception Handling**: Custom exceptions for runtime & syntax errors such as `MissingHaltException`, `DuplicateLabelException`, `InvalidInstructionException`, `InvalidRegisterException`, `InvalidArgumentsException`, `DivisionByZeroException`, and `FileNotFoundException`.
- **Comment Support**: Inline and single-line comments prefixed with `;`.

---

## 📜 Supported Instruction Set

### 1. Data Movement & Memory
| Instruction | Syntax | Description |
| :--- | :--- | :--- |
| `MOV` | `MOV R_dest, <int>` | Move an immediate integer into a register |
| `LOAD` | `LOAD R_dest, <addr>` | Load value from memory address into register |
| `STORE` | `STORE R_src, <addr>` | Store register value into memory address |

### 2. Arithmetic Operations
| Instruction | Syntax | Description |
| :--- | :--- | :--- |
| `ADD` | `ADD R_dest, R_src1, <R_src2 / int>` | Add values and store result in `R_dest` |
| `SUB` | `SUB R_dest, R_src1, <R_src2 / int>` | Subtract value from `R_src1` and store in `R_dest` |
| `MUL` | `MUL R_dest, R_src1, <R_src2 / int>` | Multiply values and store in `R_dest` |
| `DIV` | `DIV R_dest, R_src1, <R_src2 / int>` | Divide `R_src1` by value and store in `R_dest` |

### 3. Bitwise & Shift Operations
| Instruction | Syntax | Description |
| :--- | :--- | :--- |
| `AND` | `AND R_dest, R_src1, R_src2` | Bitwise AND |
| `OR` | `OR R_dest, R_src1, R_src2` | Bitwise OR |
| `XOR` | `XOR R_dest, R_src1, R_src2` | Bitwise XOR |
| `NOT` | `NOT R_dest, R_src` | Bitwise NOT (bitwise complement) |
| `SHL` | `SHL R_dest, R_src, <int>` | Shift `R_src` left by specified bits |
| `SHR` | `SHR R_dest, R_src, <int>` | Shift `R_src` right (logical) by specified bits |

### 4. Comparison & Control Flow
| Instruction | Syntax | Description |
| :--- | :--- | :--- |
| `CMP` | `CMP R1, R2` | Compare `R1` and `R2`, setting Zero & Negative flags |
| `JMP` | `JMP <label>` | Unconditional jump to label |
| `JZ` | `JZ <label>` | Jump to label if `zeroFlag` is true |
| `JN` | `JN <label>` | Jump to label if `negativeFlag` is true |
| `JNZ` | `JNZ <label>` | Jump to label if `zeroFlag` is false |

### 5. System & Output
| Instruction | Syntax | Description |
| :--- | :--- | :--- |
| `PRINT` | `PRINT R_src` | Print value of register to console |
| `HALT` | `HALT` | Stop program execution (required at the end) |

---

## 🛠️ Project Structure

```
Assembly-Interpreter/
├── src/
│   ├── main/java/com/interpreter/
│   │   ├── App.java                 # Entry point (Console interface)
│   │   ├── engine/
│   │   │   ├── Tokenizer.java       # Lexer & syntax validator
│   │   │   ├── Parser.java          # Label resolver & PC address builder
│   │   │   └── Executor.java        # Execution engine & CPU simulation
│   │   ├── model/
│   │   │   ├── Expression.java      # Representation of parsed line/token
│   │   │   └── Instruction.java     # Instruction set Enum
│   │   └── exceptions/              # Custom domain exceptions
│   └── test/java/com/interpreter/   # JUnit test suite
├── example.txt                      # Sample assembly program
├── pom.xml                          # Maven build configuration
└── README.md
```

---

## 💻 Example Assembly Program (`example.txt`)

```assembly
MOV R0, 0        ; R0 = accumulator (sum)
MOV R1, 1        ; R1 = current counter
MOV R2, 5        ; R2 = upper limit

loop:
ADD R0, R0, R1   ; sum = sum + counter
ADD R1, R1, 1    ; increment counter
CMP R1, R2       ; compare counter with limit
JNZ loop         ; repeat while counter != limit

ADD R0, R0, R2   ; add final value
PRINT R0         ; Prints: 15

STORE R0, 10     ; Store 15 in memory address 10
MOV R3, 0        ; Clear R3
LOAD R3, 10      ; Load memory[10] into R3
PRINT R3         ; Prints: 15

HALT
```

---

## ⚙️ Building & Running

### Prerequisites
- **Java JDK**: 17 or higher
- **Maven**: 3.6+

### Compile & Build
```bash
mvn clean compile
```

### Run the Application
```bash
mvn exec:java -Dexec.mainClass="com.interpreter.App"
```
When prompted:
```text
Please enter the file path:
```
Enter `example.txt` (or the absolute path to your `.txt` file).

### Run Tests
```bash
mvn test
```
