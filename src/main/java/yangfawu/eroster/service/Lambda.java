package yangfawu.eroster.service;

public interface Lambda<T, S> {

    public S exec(T data);

}
