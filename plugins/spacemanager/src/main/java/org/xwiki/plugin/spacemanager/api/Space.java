package org.xwiki.plugin.spacemanager.api;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;

import java.util.List;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: ludovic
 * Date: 28 nov. 2007
 * Time: 12:17:46
 *
 *
 * WikiSpaceName.WebPreferences
 * Object XWiki.XWikiPreferences
 * title
 * description
 * homeShortcutUrl
 * creator (creator of the WebPreference document
 * status
 */
public interface Space {
	
	/**
	 * Determines if the space already exists or is newly created
	 * @return
	 */
	public boolean isNew();

    /**
     * Determines if the space has been marked as deleted
     * @return
     */
    public boolean isDeleted();


    /**
     * Get the technical name
     * @return space name
     */
    public String getSpaceName();

    /**
	 * Gets the type of the space
	 * @return
	 */
	public String getType();
	
	/**
	 * Sets the type of the space
	 * @param type
	 * @return
	 */
	public void setType( String type );

    /**
	 * Gets the policy of the space
	 * @return
	 */
	public String getPolicy();

	/**
	 * Sets the policy of the space
	 * @param policy
	 * @return
	 */
	public void setPolicy( String policy );

    /**
	 * Gets the name of the creator of this space
	 * @return
	 */
    public String getCreator();

    /**
     * Set the name of the creator of this space
     * This should only be used to overide the creator
       @return
     */
    public void setCreator(String creator);


    /**
     * Gets the title of the space
     * @return
     */
    public String getDisplayTitle();

    /**
     * Set the display title of the space
     * This will be saved on the call to "save"
     * @param title
     */
    public void setDisplayTitle(String title);

    /**
     * Get the description of the space
     * @return
     */
    public  String getDescription();


    /**
     * Set the description title of a space
     * This will be saved on the call to "save"
     * @param description
     */
    public void setDescription(String description);

    /**
     * Get a preference of a space
     * @return
     * TODO: do we need the context?
     * @throws SpaceManagerException 
     */
    public String getPreference(String prefName) throws SpaceManagerException;


    /**
     * Get the Home shortcut URL. The shortcut URL is manual and is not handled by the XWiki server but by the frontal server
     * @return
     */
    public String getHomeShortcutURL();

    /**
     * Set the home shortcut URL
     * @param homeShortCutURL
     */
    public void setHomeShortcutURL(String homeShortCutURL);


    /**
     * Gets the Wiki Home page URL for the space
     * @return
     * @throws SpaceManagerException 
     */
    public String getHomeURL() throws SpaceManagerException;

    /**
     * Get the list of editable fields for this space
     * @return
     * @throws SpaceManagerException 
     */
    public List getFieldNames() throws SpaceManagerException;

    /**
     * Display a space field in view or edit mode
     * @param fieldName
     * @param mode
     * @return
     */
    public String display(String fieldName, String mode);

    /**
     * Save the modified space
     * @throws XWikiException
     */
    public void save() throws XWikiException;

    /**
     * Save the modified space
     * @throws XWikiException
     */
    public void saveWithProgrammingRights() throws XWikiException;

    /**
     * Update the space data from the request
     */
    public void updateSpaceFromRequest() throws SpaceManagerException;

    /**
     * Validate space data
     * @return
     * @throws SpaceManagerException
     */
    public boolean validateSpaceData() throws SpaceManagerException;

    /**
     * Set the creation date of the space
     * @param date
     */
    public void setCreationDate(Date date);
}
