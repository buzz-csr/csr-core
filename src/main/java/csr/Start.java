package csr;

import org.apache.commons.cli.*;

public class Start {

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("a", true, "action");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            String action = cmd.getOptionValue("a");
            if(action.equals("pack")){
                new Compress().zipAll();
            }else if(action.equals("unpack")){
                new Extract().unzipAll();
            }else{
                System.out.print("Missing argument -a");
            }
        } catch (ParseException e) {
            System.out.println(e);
        }
    }
}
