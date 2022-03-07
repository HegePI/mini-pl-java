package miniPL;

abstract class Errors {

    public class ScanningError extends RuntimeException {
        final int line;
        final String msg;

        public ScanningError(int line, String msg) {
            this.line = line;
            this.msg = msg;
        }

        public void printErrorMessage() {
            System.out.println("Error on line: " + this.line + "\nMessage: " + this.msg);
        }
    }

    public class ParsingError extends RuntimeException {
        final int line;
        final String msg;

        public ParsingError(int line, String msg) {
            this.line = line;
            this.msg = msg;
        }

        public void printErrorMessage() {
            System.out.println("Error on line: " + this.line + "\nMessage: " + this.msg);
        }
    }
}
