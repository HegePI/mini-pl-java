package miniPL;

public abstract class Declaration {

    abstract <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visitProgramDeclaration(ProgramDeclaration decl);

        T visitProcedureDeclaration(ProcedureDeclaration decl);

        T visitFunctionDeclaration(FunctionDeclaration decl);

        T visitVariableDeclaration(VariableDeclaration decl);

        T visitBlockDeclaration(BlockDeclaration decl);

    }

    static class ProgramDeclaration extends Declaration {

        @Override
        <T> T accept(Visitor<T> visitor) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    static class ProcedureDeclaration extends Declaration {

        @Override
        <T> T accept(Visitor<T> visitor) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    static class FunctionDeclaration extends Declaration {
        FunctionDeclaration() {
        }

        @Override
        <T> T accept(Visitor<T> visitor) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    static class VariableDeclaration extends Declaration {

        @Override
        <T> T accept(Visitor<T> visitor) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    static class BlockDeclaration extends Declaration {

        @Override
        <T> T accept(Visitor<T> visitor) {
            // TODO Auto-generated method stub
            return null;
        }
    }

}
