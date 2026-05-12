package com.interpreter.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.interpreter.exceptions.DuplicateLabelException;
import com.interpreter.exceptions.FileNotFoundException;
import com.interpreter.exceptions.InvalidArgumentsException;
import com.interpreter.exceptions.InvalidInstructionException;
import com.interpreter.exceptions.InvalidRegisterException;
import com.interpreter.exceptions.MissingHaltException;
import com.interpreter.model.Expression;
import com.interpreter.model.Instruction;

public class Tokenizer {
    public static List<Expression> tokenize(String file)
            throws MissingHaltException, FileNotFoundException, InvalidInstructionException, InvalidRegisterException,
            InvalidArgumentsException, DuplicateLabelException {

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            List<Expression> tokens = new ArrayList<>();
            HashSet<String> uniqueLabels = new HashSet<>();

            String line;
            boolean haltExists = false;
            String savedLabel = null;

            while ((line = reader.readLine()) != null) {
                String label = null;

                // ignore the comments after ';'
                line = line.contains(";") ? line.split(";")[0].trim() : line.trim();

                // if line is empty just skip (either blank line or comment line)
                if (line.isBlank())
                    continue;

                // Check if there is a label
                // label is on a separate line so just skip to next instruction
                if (line.contains(":")) {
                    savedLabel = line.split(":")[0];

                    if (uniqueLabels.contains(savedLabel)) {
                        throw new DuplicateLabelException("Label '" + savedLabel + "' is already defined");
                    }

                    uniqueLabels.add(savedLabel);
                    continue;
                }

                // [,\\s]+ means if there is a comma or whitespace,
                // + means if there is more than one
                String[] parts = line.split("[,\\s]+");

                Instruction instruction;

                // try map the string into its Instruction Enum
                try {
                    instruction = Instruction.valueOf(parts[0]);
                } catch (Exception e) {
                    throw new InvalidInstructionException(parts[0] + " is not a valid instruction");
                }

                // Check if it is a halt
                if (instruction == Instruction.HALT) {
                    haltExists = true;
                }

                // Get Arguments (need to check that they are valid registers R0 - R7)
                // check for each instruction that it has the correct number of arguments
                // labels for branching and of course we would have to
                // We need to check that jump to labels exist (but maybe we can do that from
                // executor)
                // and that there are no duplicate labels

                List<String> arguments = new ArrayList<>();

                // requires three registers
                if (instruction == Instruction.ADD || instruction == Instruction.SUB || instruction == Instruction.MUL
                        || instruction == Instruction.DIV || instruction == Instruction.AND
                        || instruction == Instruction.OR || instruction == Instruction.XOR) {

                    if (parts.length != 4) {
                        throw new InvalidArgumentsException(
                                instruction + " expects 3 arguments (" + instruction + " <reg> <reg> <reg>), but got "
                                        + (parts.length - 1));
                    }

                    String first = parts[1];
                    String second = parts[2];
                    String third = parts[3];

                    if (!isValidRegister(first) || !isValidRegister(second) || !isValidRegister(third)) {
                        throw new InvalidRegisterException(
                                instruction + ": arguments 1, 2, and 3 must be valid registers (R0-R7), but got '"
                                        + first + "', '" + second + "', '" + third + "'");
                    }

                    arguments.addAll(List.of(first, second, third));
                }

                // requires two registers
                if (instruction == Instruction.NOT || instruction == Instruction.CMP) {
                    if (parts.length != 3) {
                        throw new InvalidArgumentsException(
                                instruction + " expects 2 arguments (" + instruction + " <reg> <reg>), but got "
                                        + (parts.length - 1));
                    }

                    String first = parts[1];
                    String second = parts[2];

                    if (!isValidRegister(first) || !isValidRegister(second)) {
                        throw new InvalidRegisterException(
                                instruction + ": arguments 1 and 2 must be valid registers (R0-R7), but got '" + first
                                        + "', '" + second + "'");
                    }

                    arguments.addAll(List.of(first, second));
                }

                // requires one register
                if (instruction == Instruction.PRINT) {
                    if (parts.length != 2) {
                        throw new InvalidArgumentsException(
                                instruction + " expects 1 argument (" + instruction + " <reg>), but got "
                                        + (parts.length - 1));
                    }

                    String first = parts[1];

                    if (!isValidRegister(first)) {
                        throw new InvalidRegisterException(
                                instruction + ": argument 1 must be a valid register (R0-R7), but got '" + first + "'");
                    }

                    arguments.add(first);
                }

                // requires zero registers
                if (instruction == Instruction.HALT) {
                    if (parts.length != 1) {
                        throw new InvalidArgumentsException(
                                instruction + " expects 0 arguments, but got " + (parts.length - 1));
                    }
                }

                // requires label
                if (instruction == Instruction.JMP || instruction == Instruction.JNZ
                        || instruction == Instruction.JN || instruction == Instruction.JZ) {

                    if (parts.length != 2) {
                        throw new InvalidArgumentsException(
                                instruction + " expects 1 argument (" + instruction + " <label>), but got "
                                        + (parts.length - 1));
                    }

                    String first = parts[1];

                    if (isValidRegister(first)) {
                        throw new InvalidArgumentsException(
                                instruction + ": argument 1 must be a label, but got register '" + first
                                        + "': R0-R7 are reserved");
                    }

                    if (isInt(first)) {
                        throw new InvalidArgumentsException(
                                instruction + ": argument 1 must be a label, but got integer '" + first + "'");
                    }

                    arguments.add(first);
                }

                // requires register and value
                if (instruction == Instruction.MOV || instruction == Instruction.LOAD
                        || instruction == Instruction.STORE) {

                    if (parts.length != 3) {
                        throw new InvalidArgumentsException(
                                instruction + " expects 2 arguments (" + instruction + " <reg> <int>), but got "
                                        + (parts.length - 1));
                    }

                    String first = parts[1];
                    String second = parts[2];

                    if (!isValidRegister(first)) {
                        throw new InvalidRegisterException(
                                instruction + ": argument 1 must be a valid register (R0-R7), but got '" + first + "'");
                    }

                    if (!isInt(second)) {
                        throw new InvalidArgumentsException(
                                instruction + ": argument 2 must be an integer, but got '" + second + "'");
                    }

                    arguments.addAll(List.of(first, second));
                }

                // requires two registers and a value
                if (instruction == Instruction.SHL || instruction == Instruction.SHR) {

                    if (parts.length != 4) {
                        throw new InvalidArgumentsException(
                                instruction + " expects 3 arguments (" + instruction + " <reg> <reg> <int>), but got "
                                        + (parts.length - 1));
                    }

                    String first = parts[1];
                    String second = parts[2];
                    String third = parts[3];

                    if (!isValidRegister(first) || !isValidRegister(second)) {
                        throw new InvalidRegisterException(
                                instruction + ": arguments 1 and 2 must be valid registers (R0-R7), but got '" + first
                                        + "', '" + second + "'");
                    }

                    if (!isInt(third)) {
                        throw new InvalidArgumentsException(
                                instruction + ": argument 3 must be an integer, but got '" + third + "'");
                    }

                    arguments.addAll(List.of(first, second, third));
                }

                if (savedLabel != null) {
                    label = savedLabel;
                    savedLabel = null;
                }

                Expression expression = new Expression(label, instruction, arguments);
                tokens.add(expression);

            }

            // if label is last line with no instruction
            if (savedLabel != null) {
                throw new InvalidArgumentsException("Label '" + savedLabel + "' has no instruction");
            }

            if (!haltExists) {
                throw new MissingHaltException("You need to HALT your program");
            }

            // We can do a second pass to make sure that all our jump instructions call
            // exising valid labels

            for (Expression expression : tokens) {
                Instruction instruction = expression.getInstruction();

                if (instruction == Instruction.JMP || instruction == Instruction.JNZ
                        || instruction == Instruction.JN || instruction == Instruction.JZ) {
                    // make sure the target label exists
                    String target = expression.getArguments().get(0);
                    if (!uniqueLabels.contains(target)) {
                        throw new InvalidArgumentsException("Jump target '" + target + "' is not defined");
                    }
                }
            }

            return tokens;
        } catch (IOException e) {
            throw new FileNotFoundException(file + " is not found");
        }
    }

    private static boolean isValidRegister(String register) {
        return register.equals("R0") ||
                register.equals("R1") ||
                register.equals("R2") ||
                register.equals("R3") ||
                register.equals("R4") ||
                register.equals("R5") ||
                register.equals("R6") ||
                register.equals("R7");
    }

    private static boolean isInt(String argument) {
        try {
            Integer.parseInt(argument);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
