package csr;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Start {

    private static Logger log = LoggerFactory.getLogger(Start.class);

    public static void main(String[] args) {
        log.info("Starting CSRPacker version 1.0-BETA");
        Options options = new Options();
        options.addOption("a", true, "action");
        options.addOption("i", false, "informations");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            String action = cmd.getOptionValue("a");
            if (action.equals("pack")) {
                new Compress().zipAll("./");
                log.info("Finished");
            } else if (action.equals("unpack")) {
                new Extract().unzipAll("./");
                log.info("Finished");
            } else if(cmd.hasOption("i")){
                log.info("CSRPacker version : 1.0-BETA");
                log.info("Author : Buzz");
            } else{
                log.info("Missing mandatory parameter -a");
                log.info("Execute jar with -i for more informations");
            }
        } catch (ParseException e) {
            log.error("Error parsing command", e);
        }
    }
}
