package inventorymanagement.usermodule.service;

import inventorymanagement.usermodule.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final User user;

    /**
     * Constructor to initialize CustomUserDetails with the User entity.
     *
     * @param user The User entity
     */
    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Returns an empty collection as no roles or authorities are used in this project.
     *
     * @return An empty collection of GrantedAuthority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // No roles or authorities required
    }

    /**
     * Returns the user's hashed password.
     *
     * @return The password hash
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * Returns the user's email, which acts as the username.
     *
     * @return The user's email
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Indicates whether the user's account is non-expired.
     *
     * @return true (can be customized if needed)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user's account is non-locked.
     *
     * @return true (can be customized if needed)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials are non-expired.
     *
     * @return true (can be customized if needed)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled.
     *
     * @return true (can be customized if needed)
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
