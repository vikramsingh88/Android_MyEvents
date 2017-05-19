package vikram.mindtree.com.myevents;

import android.graphics.Bitmap;

public class TeamMemberDetails {
    private String teamName;
    private Bitmap teamLogo;
    private String teamLogoUrl;
    private Bitmap teamBanner;
    private String teamBannerUrl;

    public String getTeamLogoUrl() {
        return teamLogoUrl;
    }

    public void setTeamLogoUrl(String teamLogoUrl) {
        this.teamLogoUrl = teamLogoUrl;
    }

    public String getTeamBannerUrl() {
        return teamBannerUrl;
    }

    public void setTeamBannerUrl(String teamBannerUrl) {
        this.teamBannerUrl = teamBannerUrl;
    }

    private String teamColor;
    private Member [] members;

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Bitmap getTeamLogo() {
        return teamLogo;
    }

    public void setTeamLogo(Bitmap teamLogo) {
        this.teamLogo = teamLogo;
    }

    public Bitmap getTeamBanner() {
        return teamBanner;
    }

    public void setTeamBanner(Bitmap teamBanner) {
        this.teamBanner = teamBanner;
    }

    public String getTeamColor() {
        return teamColor;
    }

    public void setTeamColor(String teamColor) {
        this.teamColor = teamColor;
    }

    public Member[] getMembers() {
        return members;
    }

    public void setMembers(Member[] members) {
        this.members = members;
    }

    class Member {
        private String memberName;
        private String aboutMember;
        private Bitmap memberPic;
        private String imageUrl;
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getMemberName() {
            return memberName;
        }

        public void setMemberName(String memberName) {
            this.memberName = memberName;
        }

        public String getAboutMember() {
            return aboutMember;
        }

        public void setAboutMember(String aboutMember) {
            this.aboutMember = aboutMember;
        }

        public Bitmap getMemberPic() {
            return memberPic;
        }

        public void setMemberPic(Bitmap memberPic) {
            this.memberPic = memberPic;
        }
    }
}
