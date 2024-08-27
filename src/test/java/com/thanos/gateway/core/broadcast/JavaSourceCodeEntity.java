package com.thanos.gateway.core.broadcast;


import java.util.Objects;

/**
 * JavaSourceCodeEntity.java description：
 *
 * @Author laiyiyu create on 2021-04-15 17:33:25
 */
public class JavaSourceCodeEntity {

    String clazzName;

    String sourceCode;


    public JavaSourceCodeEntity(String clazzName, String sourceCode) {
        this.clazzName = clazzName;
        this.sourceCode = sourceCode;
    }

    public String getClazzName() {
        return clazzName;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JavaSourceCodeEntity that = (JavaSourceCodeEntity) o;
        return Objects.equals(clazzName, that.clazzName) &&
                Objects.equals(sourceCode, that.sourceCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazzName, sourceCode);
    }
}
