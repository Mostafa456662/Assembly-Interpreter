package com.interpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import com.interpreter.engine.*;
import com.interpreter.model.Expression;

public class App {
    public static void main(String[] args) {
        // Load file
        //
        // Pass it to tokenizer
        //
        // pass the result to parser
        //
        // get the hashmaps from parser
        //
        // pass them to executor
        //
        // loop until halt or final pc

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the file path: ");
        String filePath = scanner.nextLine();

        try {
            List<Expression> expressions = Tokenizer.tokenize(filePath);

            HashMap<Integer, Expression> instructions = Parser.assignPC(expressions);

            int[] memory = new int[256];
            int[] registers = new int[8];

            Executor executor = new Executor(memory, registers, instructions, Parser.resolveLabels(expressions));
            executor.execute();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
