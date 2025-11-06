package devdojo.compiler.security;

public interface ISecurityFilter {

  boolean check(final String methodCode) throws Exception;

}
