package org.olahammed.SpringStarter.util.constants;

public enum Priviledges {
    RESET_ANY_USER_PASSWORD(1l, "RESET_ANY_USER_PASSWORD"),
    ACCESS_ADMIN_PANEL(2l, "ACCESS_ADMIN_PANEL"),
    SERVE_AS_EDITOR(3l, "EDITOR_PRIVILEDGE"),
    SERVE_AS_ADMIN(4l, "ADMIN_PRIVILEDGE"),
    SERVE_AS_SUPER_USER(5l, "ALPHA_PRIVILEDGE");

    private Long Id;
    private String priviledge;

    private Priviledges(Long Id, String priviledge){
        this.Id = Id;
        this.priviledge = priviledge;
    }

    public Long getId(){
        return Id;
    }

    public String getPriviledge(){
        return priviledge;
    }
}
