package chocolateam.fr.Utils;

public class Contact {
    private String phoneNumber;
    private boolean isFavorite;

    public Contact(String phoneNumber, boolean isFavorite) {
        this.phoneNumber = phoneNumber;
        this.isFavorite = isFavorite;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
