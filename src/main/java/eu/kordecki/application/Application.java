package eu.kordecki.application;

import eu.kordecki.service.Converter;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Wrong arguments: arg[0] is input file path, arg[1] is input file out");
            System.exit(1);
        }
        System.err.println("Starting conversion");
        Converter converter = new Converter(args[0], args[1]);
        converter.saveFile();
    }


}
