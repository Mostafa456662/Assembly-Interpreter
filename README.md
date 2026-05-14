# Assembly Interpreter

This project is a Java 17 / Maven assembly-language interpreter. The code currently includes:

- A command-line entry point that asks the user for an assembly file path.
- A tokenizer/validator that reads assembly source code and converts each instruction into an `Expression`.
- A model layer for parsed expressions and supported instruction names.
- Custom exception classes for user-facing interpreter errors.
- Parser and executor classes that are present but not implemented yet.

## Project Structure

```text
src/main/java/com/interpreter/
  App.java                         Application entry point
  engine/
    Tokenizer.java                 Reads and validates assembly source files
    Parser.java                    Intended to map labels and program counters
    Executor.java                  Intended to execute parsed instructions
  model/
    Expression.java                Represents one parsed instruction
    Instruction.java               Enum of supported instruction names
  exceptions/
    *.java                         Custom checked exception classes

src/test/java/com/interpreter/
  AppTest.java                     Placeholder JUnit test

example.txt                        Example assembly program
pom.xml                            Maven project configuration
```

## Running the Project

The Maven `exec-maven-plugin` is configured to run `com.interpreter.App`.

```bash
mvn exec:java
```

The program prompts for a file path:

```text
Please enter the file path:
```

Input format:

- A single line containing the path to an assembly source file.

Output format:

- On success, the interpreter should eventually execute the program.
- At the moment, execution is incomplete because `Parser.assignPC(...)` returns `null` and `Executor.execute()` is empty.
- On failure, the program prints the exception message to standard output.

## Assembly Source Format

The tokenizer expects one instruction per line. Arguments may be separated by commas, spaces, or both.

```asm
MOV R0, 5
ADD R1, R0, R2
PRINT R1
HALT
```

Comments start with `;`. Everything after `;` on a line is ignored.

```asm
MOV R0, 5 ; this is a comment
```

Blank lines and comment-only lines are ignored.

Labels are written with a trailing colon and must currently appear on their own line immediately before the instruction they label.

```asm
loop:
ADD R0, R0, R1
JNZ loop
```

Every program must contain `HALT`.

## Supported Registers

The tokenizer accepts only these register names:

```text
R0 R1 R2 R3 R4 R5 R6 R7
```

Register names are case-sensitive.

## Supported Instructions and Tokenizer Argument Formats

These formats describe what `Tokenizer.tokenize(...)` currently accepts.

| Instruction | Format | Meaning intended by instruction name |
| --- | --- | --- |
| `MOV` | `MOV <reg> <int>` | Move an integer value into a register |
| `ADD` | `ADD <reg> <reg> <reg>` | Add two registers |
| `SUB` | `SUB <reg> <reg> <reg>` | Subtract two registers |
| `MUL` | `MUL <reg> <reg> <reg>` | Multiply two registers |
| `DIV` | `DIV <reg> <reg> <reg>` | Divide two registers |
| `AND` | `AND <reg> <reg> <reg>` | Bitwise AND |
| `OR` | `OR <reg> <reg> <reg>` | Bitwise OR |
| `XOR` | `XOR <reg> <reg> <reg>` | Bitwise XOR |
| `NOT` | `NOT <reg> <reg>` | Bitwise NOT |
| `SHL` | `SHL <reg> <reg> <int>` | Shift left by an integer amount |
| `SHR` | `SHR <reg> <reg> <int>` | Shift right by an integer amount |
| `LOAD` | `LOAD <reg> <int>` | Load from a memory address |
| `STORE` | `STORE <reg> <int>` | Store to a memory address |
| `CMP` | `CMP <reg> <reg>` | Compare two registers and set flags |
| `JMP` | `JMP <label>` | Jump to label |
| `JZ` | `JZ <label>` | Jump if zero flag is set |
| `JN` | `JN <label>` | Jump if negative flag is set |
| `JNZ` | `JNZ <label>` | Jump if zero flag is not set |
| `PRINT` | `PRINT <reg>` | Print a register value |
| `HALT` | `HALT` | Stop execution |

Important current limitation: arithmetic instructions such as `ADD`, `SUB`, `MUL`, and `DIV` currently require all three operands to be registers. For example, `ADD R1, R1, 1` in `example.txt` does not match the tokenizer rules because the third argument is an integer, not a register.

## Data Model

### `Expression`

An `Expression` represents one parsed assembly instruction.

Fields:

- `label`: `String`; the label attached to this instruction, or `null` when the instruction has no label.
- `instruction`: `Instruction`; the enum value for the operation.
- `arguments`: `List<String>`; raw string arguments after validation.

Example:

```java
new Expression("loop", Instruction.ADD, List.of("R0", "R0", "R1"))
```

String format returned by `toString()`:

```text
["loop", "ADD", "R0, R0, R1"]
```

## Function Reference

### `App`

#### `public static void main(String[] args)`

Application entry point.

Input:

- `args`: command-line arguments. The current implementation does not use them.
- Standard input: reads one line from the user. This line should be the path to an assembly file.

Process:

1. Prompts the user for a file path.
2. Calls `Tokenizer.tokenize(filePath)`.
3. Calls `Parser.assignPC(expressions)`.
4. Creates `memory` as `int[256]`.
5. Creates `registers` as `int[8]`.
6. Creates an `Executor`.
7. Calls `executor.execute()`.

Output:

- Prints the file-path prompt to standard output.
- If an exception is thrown, prints the exception message to standard output.
- Returns `void`.

Current limitation:

- Since `Parser.assignPC(...)` returns `null` and `Executor.execute()` is empty, the full interpreter flow is not functional yet.

### `Tokenizer`

#### `public static List<Expression> tokenize(String file)`

Reads an assembly source file, validates its syntax, and returns validated expressions.

Input:

- `file`: path to the source file as a `String`.

Accepted source format:

- One instruction per line.
- Comments begin with `;`.
- Blank lines are ignored.
- Labels use `label:` and must be on a separate line before an instruction.
- Registers must be `R0` through `R7`.
- Integer arguments must be parseable by `Integer.parseInt(...)`.
- At least one `HALT` instruction is required.
- Jump targets must reference labels that were defined somewhere in the file.

Output:

- Returns `List<Expression>`.
- Each `Expression` contains:
  - optional label,
  - parsed `Instruction`,
  - validated argument strings.

Throws:

- `MissingHaltException`: no `HALT` instruction exists.
- `FileNotFoundException`: file cannot be opened or read.
- `InvalidInstructionException`: instruction name is not in the `Instruction` enum.
- `InvalidRegisterException`: a register argument is not one of `R0` through `R7`.
- `InvalidArgumentsException`: wrong argument count, wrong argument type, invalid jump target, or label without instruction.
- `DuplicateLabelException`: the same label is defined more than once.

#### `private static boolean isValidRegister(String register)`

Checks whether a string is a valid register name.

Input:

- `register`: string to check.

Output:

- Returns `true` if the input is exactly one of `R0`, `R1`, `R2`, `R3`, `R4`, `R5`, `R6`, or `R7`.
- Returns `false` otherwise.

#### `private static boolean isInt(String argument)`

Checks whether a string can be parsed as a Java `int`.

Input:

- `argument`: string to check.

Output:

- Returns `true` if `Integer.parseInt(argument)` succeeds.
- Returns `false` if parsing throws `NumberFormatException`.

### `Parser`

#### `public static HashMap<String, Expression> resolveLabels(List<Expression> expressions)`

Intended to build a lookup table from label names to expressions.

Input:

- `expressions`: parsed expressions from the tokenizer.

Expected output:

- Intended return type is `HashMap<String, Expression>`, where:
  - key: label name,
  - value: expression attached to that label.

Current output:

- Always returns `null`.

#### `public static HashMap<Integer, Expression> assignPC(List<Expression> expressions)`

Intended to assign program-counter addresses to expressions.

Input:

- `expressions`: parsed expressions from the tokenizer.

Expected output:

- Intended return type is `HashMap<Integer, Expression>`, where:
  - key: program counter value,
  - value: expression at that program counter.

Current output:

- Always returns `null`.

### `Executor`

#### `public Executor(int[] memory, int[] registers, HashMap<Integer, Expression> instructions)`

Constructs an executor with memory, registers, flags, and parsed instructions.

Input:

- `memory`: integer memory array. `App` currently passes `new int[256]`.
- `registers`: integer register array. `App` currently passes `new int[8]`.
- `instructions`: map from program-counter values to expressions.

Output:

- Creates an `Executor` object.
- Initializes `zeroFlag` to `false`.
- Initializes `negativeFlag` to `false`.

#### `public void execute()`

Intended to run the instruction map until `HALT` or the end of the program.

Input:

- No direct parameters.
- Uses executor fields: `memory`, `registers`, `instructions`, `zeroFlag`, and `negativeFlag`.

Expected output:

- Should execute instructions and update registers, memory, flags, and program counter.
- May print values for `PRINT`.

Current output:

- Does nothing and returns `void`.

#### `private void mov()`

Intended to execute `MOV <reg> <int>`.

Input:

- No direct parameters.
- Expected to use the current instruction's arguments.

Expected output:

- Should store the integer value in the target register.

Current output:

- Does nothing.

#### `private void add()`

Intended to execute `ADD <reg> <reg> <reg>`.

Input:

- No direct parameters.
- Expected operands: destination register, left register, right register.

Expected output:

- Should store `left + right` in the destination register.

Current output:

- Does nothing.

#### `private void sub()`

Intended to execute `SUB <reg> <reg> <reg>`.

Input:

- No direct parameters.
- Expected operands: destination register, left register, right register.

Expected output:

- Should store `left - right` in the destination register.

Current output:

- Does nothing.

#### `private void mul()`

Intended to execute `MUL <reg> <reg> <reg>`.

Input:

- No direct parameters.
- Expected operands: destination register, left register, right register.

Expected output:

- Should store `left * right` in the destination register.

Current output:

- Does nothing.

#### `private void div()`

Intended to execute `DIV <reg> <reg> <reg>`.

Input:

- No direct parameters.
- Expected operands: destination register, numerator register, denominator register.

Expected output:

- Should store `numerator / denominator` in the destination register.
- Should probably throw `DivisionByZeroException` when the denominator is zero, but this is not implemented yet.

Current output:

- Does nothing.

#### `private void and()`

Intended to execute `AND <reg> <reg> <reg>`.

Input:

- No direct parameters.
- Expected operands: destination register, left register, right register.

Expected output:

- Should store `left & right` in the destination register.

Current output:

- Does nothing.

#### `private void or()`

Intended to execute `OR <reg> <reg> <reg>`.

Input:

- No direct parameters.
- Expected operands: destination register, left register, right register.

Expected output:

- Should store `left | right` in the destination register.

Current output:

- Does nothing.

#### `private void xor()`

Intended to execute `XOR <reg> <reg> <reg>`.

Input:

- No direct parameters.
- Expected operands: destination register, left register, right register.

Expected output:

- Should store `left ^ right` in the destination register.

Current output:

- Does nothing.

#### `private void not()`

Intended to execute `NOT <reg> <reg>`.

Input:

- No direct parameters.
- Expected operands: destination register, source register.

Expected output:

- Should store `~source` in the destination register.

Current output:

- Does nothing.

#### `private void shl()`

Intended to execute `SHL <reg> <reg> <int>`.

Input:

- No direct parameters.
- Expected operands: destination register, source register, shift amount.

Expected output:

- Should store `source << amount` in the destination register.

Current output:

- Does nothing.

#### `private void shr()`

Intended to execute `SHR <reg> <reg> <int>`.

Input:

- No direct parameters.
- Expected operands: destination register, source register, shift amount.

Expected output:

- Should store `source >> amount` in the destination register.

Current output:

- Does nothing.

#### `private void load()`

Intended to execute `LOAD <reg> <int>`.

Input:

- No direct parameters.
- Expected operands: destination register, memory address.

Expected output:

- Should copy `memory[address]` into the destination register.
- Should probably throw `InvalidMemoryAddressException` for invalid addresses, but this is not implemented yet.

Current output:

- Does nothing.

#### `private void store()`

Intended to execute `STORE <reg> <int>`.

Input:

- No direct parameters.
- Expected operands: source register, memory address.

Expected output:

- Should copy the source register value into `memory[address]`.
- Should probably throw `InvalidMemoryAddressException` for invalid addresses, but this is not implemented yet.

Current output:

- Does nothing.

#### `private void cmp()`

Intended to execute `CMP <reg> <reg>`.

Input:

- No direct parameters.
- Expected operands: left register, right register.

Expected output:

- Should compare the two register values.
- Should update `zeroFlag` and `negativeFlag`.

Current output:

- Does nothing.

#### `private void jmp()`

Intended to execute `JMP <label>`.

Input:

- No direct parameters.
- Expected operand: label.

Expected output:

- Should set the program counter to the instruction associated with the label.

Current output:

- Does nothing.

#### `private void jz()`

Intended to execute `JZ <label>`.

Input:

- No direct parameters.
- Expected operand: label.

Expected output:

- Should jump when `zeroFlag` is `true`.

Current output:

- Does nothing.

#### `private void jn()`

Intended to execute `JN <label>`.

Input:

- No direct parameters.
- Expected operand: label.

Expected output:

- Should jump when `negativeFlag` is `true`.

Current output:

- Does nothing.

#### `private void jnz()`

Intended to execute `JNZ <label>`.

Input:

- No direct parameters.
- Expected operand: label.

Expected output:

- Should jump when `zeroFlag` is `false`.

Current output:

- Does nothing.

#### `private void print()`

Intended to execute `PRINT <reg>`.

Input:

- No direct parameters.
- Expected operand: source register.

Expected output:

- Should print the source register's integer value to standard output.

Current output:

- Does nothing.

#### `private void halt()`

Intended to execute `HALT`.

Input:

- No direct parameters.

Expected output:

- Should stop program execution.

Current output:

- Does nothing.

### `Expression`

#### `public Expression(String label, Instruction instruction, List<String> arguments)`

Creates an expression object.

Input:

- `label`: label string, or `null`.
- `instruction`: parsed `Instruction`.
- `arguments`: validated argument strings.

Output:

- Creates a new `Expression` instance with the supplied values.

#### `public String getLabel()`

Input:

- No parameters.

Output:

- Returns the expression label as a `String`.
- Returns `null` if the expression has no label.

#### `public Instruction getInstruction()`

Input:

- No parameters.

Output:

- Returns the expression instruction as an `Instruction` enum value.

#### `public List<String> getArguments()`

Input:

- No parameters.

Output:

- Returns the expression argument list as `List<String>`.

#### `public String toString()`

Input:

- No parameters.

Output:

- Returns a string in this format:

```text
["<label>", "<instruction>", "<arg1>, <arg2>, ..."]
```

Example:

```text
["loop", "ADD", "R0, R0, R1"]
```

### `Instruction`

`Instruction` is an enum, so it has no custom methods in this codebase. Its values define the legal instruction names accepted by `Tokenizer.tokenize(...)`:

```text
MOV ADD SUB MUL DIV AND OR XOR NOT SHL SHR LOAD STORE CMP JMP JZ JN JNZ PRINT HALT
```

### Exception Classes

All custom exception classes extend `Exception` and follow the same constructor pattern.

#### `public DivisionByZeroException(String message)`

Input:

- `message`: error message.

Output:

- Creates a checked exception whose message is available through `getMessage()`.

#### `public DuplicateLabelException(String message)`

Input:

- `message`: error message.

Output:

- Creates a checked exception whose message is available through `getMessage()`.

#### `public FileNotFoundException(String message)`

Input:

- `message`: error message.

Output:

- Creates a checked exception whose message is available through `getMessage()`.

#### `public InvalidArgumentsException(String message)`

Input:

- `message`: error message.

Output:

- Creates a checked exception whose message is available through `getMessage()`.

#### `public InvalidInstructionException(String message)`

Input:

- `message`: error message.

Output:

- Creates a checked exception whose message is available through `getMessage()`.

#### `public InvalidMemoryAddressException(String message)`

Input:

- `message`: error message.

Output:

- Creates a checked exception whose message is available through `getMessage()`.

#### `public InvalidRegisterException(String message)`

Input:

- `message`: error message.

Output:

- Creates a checked exception whose message is available through `getMessage()`.

#### `public MissingHaltException(String message)`

Input:

- `message`: error message.

Output:

- Creates a checked exception whose message is available through `getMessage()`.

### `AppTest`

#### `public void shouldAnswerWithTrue()`

Placeholder JUnit test.

Input:

- No parameters.

Output:

- Asserts that `true` is true.
- Does not test interpreter behavior yet.

## Current Implementation Status

Implemented:

- File reading.
- Comment and blank-line handling.
- Instruction-name validation.
- Register validation.
- Argument-count validation.
- Integer argument validation.
- Duplicate-label detection.
- Required-`HALT` detection.
- Jump-target label validation.
- `Expression` data model.
- Custom exception classes.

Not implemented yet:

- Label resolution in `Parser.resolveLabels(...)`.
- Program-counter assignment in `Parser.assignPC(...)`.
- Instruction execution in `Executor.execute()`.
- All individual executor instruction methods.
- Real interpreter tests.

