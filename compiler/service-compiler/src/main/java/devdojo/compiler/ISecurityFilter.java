package devdojo.compiler;

public interface ISecurityFilter {

  boolean check(final String methodCode) throws Exception;

}
