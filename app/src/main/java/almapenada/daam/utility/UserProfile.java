package almapenada.daam.utility;


import java.net.URL;
import java.util.Date;

public class UserProfile {



    private String idUser;

    private String name;

    private String email;

    private String password;

    private String telefone;

    private String facebookID;

    private String gender;

    private String descricao;

    private Date ultimoAcesso;

    private Date dataAlteracao;

    private Date dataCriacao;

    private String picture;

    private URL pictureURL;

    public UserProfile(String id, String name, String email, String telefone, String picture ) {
        this.idUser = id;
        this.name = name;
        this.email = email;
        this.telefone = telefone;
        this.picture = picture;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getFacebookID() {
        return facebookID;
    }

    public void setFacebookID(String facebookID) {
        this.facebookID = facebookID;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getUltimoAcesso() {
        return ultimoAcesso;
    }

    public void setUltimoAcesso(Date ultimoAcesso) {
        this.ultimoAcesso = ultimoAcesso;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public URL getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(URL pictureURL) {
        this.pictureURL = pictureURL;
    }

}
