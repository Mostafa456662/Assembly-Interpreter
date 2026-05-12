package com.interpreter;

import java.util.HashMap;
import java.util.List;

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

        String file_txt = "example.txt";
        try {
            List<Expression> expressions = Tokenizer.tokenize(file_txt);

            HashMap<Integer, Expression> instructions = Parser.assignPC(expressions);
            HashMap<String, Expression> labels = Parser.resolveLabels(expressions);

            int[] memory = new int[256];
            int[] registers = new int[8];

            Executor executor = new Executor(memory, registers, instructions, labels);
            executor.execute();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
