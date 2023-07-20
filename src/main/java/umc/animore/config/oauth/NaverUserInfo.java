package umc.animore.config.oauth;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo{
    private Map<String, Object> attributes;




    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;

    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }


    public String getGender(){ return (String) attributes.get("gender"); }

}

