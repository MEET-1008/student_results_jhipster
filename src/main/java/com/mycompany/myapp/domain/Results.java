package com.mycompany.myapp.domain;

import com.mycompany.myapp.domain.enumeration.pass_faill;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Results.
 */
@Document(collection = "results")
public class Results implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("java")
    private String java;

    @Field("python")
    private String python;

    @Field("flutter")
    private String flutter;

    @Field("student_id")
    private String student_id;

    @Field("avg")
    private Double avg;

    @Field("total_marks")
    private Double total_marks;

    @Field("result")
    private pass_faill result;

    @Field("user")
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Results id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJava() {
        return this.java;
    }

    public Results java(String java) {
        this.setJava(java);
        return this;
    }

    public void setJava(String java) {
        this.java = java;
    }

    public String getPython() {
        return this.python;
    }

    public Results python(String python) {
        this.setPython(python);
        return this;
    }

    public void setPython(String python) {
        this.python = python;
    }

    public String getFlutter() {
        return this.flutter;
    }

    public Results flutter(String flutter) {
        this.setFlutter(flutter);
        return this;
    }

    public void setFlutter(String flutter) {
        this.flutter = flutter;
    }

    public String getStudent_id() {
        return this.student_id;
    }

    public Results student_id(String student_id) {
        this.setStudent_id(student_id);
        return this;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public Double getAvg() {
        return this.avg;
    }

    public Results avg(Double avg) {
        this.setAvg(avg);
        return this;
    }

    public void setAvg(Double avg) {
        this.avg = avg;
    }

    public Double getTotal_marks() {
        return this.total_marks;
    }

    public Results total_marks(Double total_marks) {
        this.setTotal_marks(total_marks);
        return this;
    }

    public void setTotal_marks(Double total_marks) {
        this.total_marks = total_marks;
    }

    public pass_faill getResult() {
        return this.result;
    }

    public Results result(pass_faill result) {
        this.setResult(result);
        return this;
    }

    public void setResult(pass_faill result) {
        this.result = result;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Results user(User user) {
        this.setUser(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Results)) {
            return false;
        }
        return id != null && id.equals(((Results) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Results{" +
            "id=" + getId() +
            ", java='" + getJava() + "'" +
            ", python='" + getPython() + "'" +
            ", flutter='" + getFlutter() + "'" +
            ", student_id='" + getStudent_id() + "'" +
            ", avg=" + getAvg() +
            ", total_marks=" + getTotal_marks() +
            ", result='" + getResult() + "'" +
            "}";
    }
}
