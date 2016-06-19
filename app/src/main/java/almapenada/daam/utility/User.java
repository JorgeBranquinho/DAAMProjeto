package almapenada.daam.utility;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;

/**
 * Created by Asus on 01/04/2016.
 */
public class User implements Serializable{

    private int idUser;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String telefone;

    private String facebookID;

    private String gender;

    private String descricao;

    private Date ultimoAcesso;

    private Date dataAlteracao;

    private Date dataCriacao;

    private Bitmap picture;

    private URL pictureURL;

    private Drawable pictureDrawable;

    private String phone;

    private Boolean pushFlag = false;

    private Boolean infpFlag = false;

    private Boolean userFromFB = false;

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public URL getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(URL pictureURL) {
        this.pictureURL = pictureURL;
    }

    public void setPictureDrawable(Drawable pictureDrawable) {
        this.pictureDrawable = pictureDrawable;
    }

    public Drawable getPictureDrawable() {
        return pictureDrawable;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public Boolean getPushFlag() {
        return pushFlag;
    }

    public void setPushFlag(Boolean pushFlag) {
        this.pushFlag = pushFlag;
    }

    public Boolean getInfpFlag() {
        return infpFlag;
    }

    public void setInfpFlag(Boolean infpFlag) {
        this.infpFlag = infpFlag;
    }

    public Boolean getUserFromFB() {
        return userFromFB;
    }

    public void setUserFromFB(Boolean userFromFB) {
        this.userFromFB = userFromFB;
    }
}
