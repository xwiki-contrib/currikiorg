#Curriki MembershipDrive

# Dev Doc
The MembershipDrive consists of one main page: the LandingPage. If the LandingPage is visited by a Guest (not logged
in user) it provides the functionality to log in via Facebook or Goolge social authentication or an existing
Curriki account. If a known user logs in via Google, Facebook or an existing account they get redirected to the
LandingPage with a button to download a resource. If a never seen Guest logs in via Google or Facebook, the
MembershipDrive automatically registers the new user (no registration form is shown to the user) and the authentication
system sends an email with the new username and password to the new user. After that these new users are redirected to
the LandingPage with the download button too. The link for the MembershipDrive LandingPage is:

    http://CURRIKI-HOST/xwiki/bin/view/MembershipDrive/LandingPage?xpage=plain


## Configure the Resource to Download
The resource which is downloaded when hitting the download button can be configured in the attachment list of the
LandingPage

    http://CURRIKI-HOST/xwiki/bin/view/MembershipDrive/LandingPage?viewer=attachments

Just upload another PDF and the users can download the new file.

## Change the Content of the LandingPage
With the here implemented concept of the MembershipDrive content changes are rather easy. Just edit the LandingPage in
xwiki editor mode.

    http://CURRIKI-HOST/xwiki/bin/edit/MembershipDrive/LandingPage?editor=wiki

The complete set of pages is:

- MembershipDrive/LandingPage (the main page including all other)
- MembershipDrive/Logins (The part of the LandingPage when users are not logged in)
- MembershipDrive/ResourceDownload (The part of the LandingPage when users are logged in)
- MembershipDrive/CurrikiLogin (The modal for the curriki login)
- MembershipDrive/styles.vm (Styles to include into the landing page, not a nice solution but better than have attached
css)

The complete set of images is:

- campaign_logo.png
- curriki-logo.png
- facebook_logo_86x86.png
- google_logo_86x86.png


## Change Social Sharing Text
The text which is pasted into the popups of the social sharing buttons is influenced by
the title of the landing page and some meta tags in the header.

    ...
    <head>
      <title>Title Here</title>

      <meta property="og:title" content="Title Here" />
      <meta property="og:description" content="Description Here" />
    </head>
    ...