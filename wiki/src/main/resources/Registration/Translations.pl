<?xml version="1.0" encoding="UTF-8"?>

<xwikidoc>
<web>Registration</web>
<name>Translations</name>
<language>pl</language>
<defaultLanguage></defaultLanguage>
<translation>1</translation>
<parent></parent>
<creator>XWiki.mzielinski</creator>
<author>XWiki.mzielinski</author>
<customClass></customClass>
<contentAuthor>XWiki.mzielinski</contentAuthor>
<creationDate>1249077764000</creationDate>
<date>1249595822000</date>
<contentUpdateDate>1249595822000</contentUpdateDate>
<version>6.1</version>
<title></title>
<template></template>
<defaultTemplate></defaultTemplate>
<validationScript></validationScript>
<comment>Register polish - more changes</comment>
<minorEdit>false</minorEdit>
<syntaxId>xwiki/1.0</syntaxId>
<object>
<class>
<name>XWiki.TagClass</name>
<customClass></customClass>
<customMapping></customMapping>
<defaultViewSheet></defaultViewSheet>
<defaultEditSheet></defaultEditSheet>
<defaultWeb></defaultWeb>
<nameField></nameField>
<validationScript></validationScript>
<tags>
<cache>0</cache>
<displayType>select</displayType>
<multiSelect>1</multiSelect>
<name>tags</name>
<number>1</number>
<prettyName>Tags</prettyName>
<relationalStorage>1</relationalStorage>
<separator> </separator>
<separators> ,|</separators>
<size>30</size>
<unmodifiable>0</unmodifiable>
<values></values>
<classType>com.xpn.xwiki.objects.classes.StaticListClass</classType>
</tags>
</class>
<name>Registration.Translations</name>
<number>0</number>
<className>XWiki.TagClass</className>
<property>
<tags/>
</property>
</object>
<content>#&lt;pre&gt;

#NEW FORGOT INFO KEYS (for 1.8.5 login release)

forgot.title=Zapomniałem swoich danych
forgot.description=Nie pamiętasz jaki był Twój login, albo hasło. Zdarza się najlepszym z nas. Wpisz adres e-mail, który podałeś podczas rejestracji, a my pomożemy Ci się zalogować (pamiętaj o małych i wielkich literach w loginie i haśle).
forgot.enter_email=Podaj proszę swój adres e-mail:
forgot.select_info=Nie pamiętasz swojego loginu lub hasła?
forgot.login.title=Login
forgot.password.title=Hasło
forgot.submit_button=Zaloguj
forgot.error.email_mandatory=Musisz podać adres e-mail.
forgot.error.email_invalid=Musisz podać poprawny adres e-mail.
forgot.error.no_user=Niestety, nie ma użytkownika przypisanego do adresu {0}
forgot.error.multiple_users=Jest kilka kont przypisanych do tego adresu e-mail.


#UN-CHANGED FORGOT INFO KEYS (remain the same for 1.8.5 login release)

forgotPassword.emailSent=Wysłano hasło dla {0}.
forgotUsername.emailSent=Wysłano login dla {0}.
forgotPassword.enterPassword=Możesz teraz wprowadzić nowe hasło (pamiętaj o małych i wielkich literach):
forgotPassword.enterPassword.prompt=Wprowadź nowe hasło (pamiętaj o małych i wielkich litearch):
forgotPassword.enterPasswordConfirm.prompt=Proszę potwierdzić nowe hasło: Please confirm the new password:
forgotPassword.Go=Dalej »
forgotPassword.passwordChanged=Hasło użytkownika {0} zostało zmienione.
forgotPassword.youCanLogIn=Możesz się teraz zalogować.
forgotPassword.invalidLink=Niestety link, który wybrałeś jest nieaktywny.
forgotPassword.passwordsDoesNotMatch=Hasło nie pasuje do wybranej przez Ciebie nazwy użytkownika. Spróbuj się zalogować ponownie (pamiętaj, że system rozróżnia wielkość liter)


#OLD FORGOT USERNAME/PASSWORD KEYS (to remove after 1.8.5 login release)

forgotPassword.title=Zapomniałem hasła
forgotPassword.forgotUnameLink=Zapomniałem mój login
forgotPassword.infos=Nie pamiętasz jaki był Twój login, albo hasło. Zdarza się najlepszym z nas. Wpisz adres e-mail, który podałeś podczas rejestracji, a my pomożemy Ci się zalogować (pamiętaj o małych i wielkich literach w loginie i haśle).
forgotPassword.enterEmail=Wpisz swój adres e-mail: Please enter your email address:
forgotPassword.noUser=Niestety, nie ma użytkownika przypisanego do adresu {0}
forgotPassword.multipleUsers=Jest kilka kont przypisanych do tego adresu e-mail. 
forgotPassword.email.mandatory=Musisz podać adres e-mail.
forgotPassword.email.invalid=Wpisz poprawny adres e-mail.
forgotUsername.title=Zapomniałeś loginu? 
forgotUsername.forgotPwdLink=Zapomniałeś hasła? 
forgotUsername.infos=Nie pamiętasz jaki był Twój login, albo hasło. Zdarza się najlepszym z nas. Wpisz adres e-mail, który podałeś podczas rejestracji, a my pomożemy Ci się zalogować (pamiętaj o małych i wielkich literach w loginie i haśle).
forgotUsername.enterEmail=Wpisz swój adres e-mail:
forgotUsername.noUser=Niestety, nie ma użytkownika przypisanego do adresu {0}
forgotUsername.email.mandatory=Musisz podać adres e-mail.
forgotUsername.email.invalid=Wpisz poprawny adres e-mail.



#Main.ForgotPasswordEmail

forgotPasswordEmail.header=Od: {0}Do: {1}Temat: Twoje hasło na Curriki.org
forgotPasswordEmail.part1=Witaj {0} {1},
forgotPasswordEmail.part2=Nie pamiętasz jakie było Twoje hasło? Zdarza się najlepszym z nas. Dla Twojego bezpieczeństwa przechowujemy jedynie zakodowaną wersję hasła. Dlatego nie możemy Ci go podać. Wystarczy jednak, że klikniesz na ten link {0}, a Twoje stare hasło zostanie skasowane i będziesz mógł wybrać nowe.
forgotPasswordEmail.part3=Twoje stare hasło zostanie skasowane, a Ty będziesz mógł się zalogować przy użyciu nowego hasła (pamiętaj o małych i wielkich literach)


#Main.ForgotUsernameEmail{0}

forgotUsernameEmail.header=Od: {0}Do: {1}Temat: Twój login na Curriki.org
forgotUsernameEmail.part1=Witaj {0} {1},
forgotUsernameEmail.part2=Nie pamiętasz jaki masz login? Zdarza się najlepszym z nas. Podczas rejestracji określiłeś się jako '{0}'. Pamiętaj o małych i wielkich literach. Jeśli zapomniałeś też swojego hasła, kliknij na "Zapomniałem hasła" na stronie głównej curriki.org, a my pomożemy Ci się zalogować (pamiętaj o małych i wielkich literach).



#Main.ForgotUsernamesEmail

forgotUsernamesEmail.part1=Drogi użytkowniku Curriki.org,
forgotUsernamesEmail.part2=Nie pamiętasz jaki masz login? Zdarza się najlepszym z nas. Do Twojego adresu e-mail przypisanych jest kilka nazw użytkownika: '${names}'. Jeśli zapomniałeś też swojego hasła, kliknij na "Zapomniałem hasła" na stronie głównej curriki.org, a my pomożemy Ci się zalogować (pamiętaj o małych i wielkich literach).

#LOGIN REDIRECT (New keys for 1.8.5 Login release)

redirect.message.title=Musisz podać swój login.
redirect.message.description=Strona którą chcesz obejrzeć jest dostępna jedynie dla użytkowników Curriki. Aby ją przejrzec musisz się zalogować. Aby to zrobić wpisz swój login i hasło we włąściwym miejscu na górze strony (Jeśli próbowałeś dodać nowe materiały, lub edytować już istniejące czynność ta nie zostanie zapisane i niestety będziesz musiał ją powtórzyć).
redirect.error.no_info=Nie podałeś swojego loginu i hasła.
redirect.error.invalid_username=Nie ma użytkownika o takim loginie
redirect.error.invalid_password=Podane przez Ciebie hasło jest nieprawidłowe



#REGISTRATION PAGE (new keys for 1.8.5 Login release)
##Selection values for the fields Subjects, Ed levels, Country, &amp; Language all use the global trans values.

register.curriki_intro.title=Dołącz do Curriki
register.curriki_intro.description=Wystarczy kilka minut aby wypełnić formularz rejestracyjny i zostać jednym z członków społeczności Curriki. Chcesz dowiedzieć się więcej. Wszystkie informacje znajdziesz tu:
register.curriki_intro.link1_text=Dlaczego Curriki jest takie ważne?
register.curriki_intro.link1_rollover=Nasza strona pozwala swobodnie wymieniać i rozbudowywać darmowe plany lekcyjne, programy nauczania, a także podręczniki.&lt;br&gt;&lt;br&gt;Materiały na naszej stronie są zawsze i wszędzie dostępne przez internet.&lt;br&gt;&lt;br&gt;Nasza społeczność działa globalnie. Umożliwiamy wymianę doświadczeń i pomysłów ludziom z całego świata.&lt;br&gt;&lt;br&gt;Kliknij i dowiedz się więcej.
register.curriki_intro.link2_text=Co będziesz mógł zrobić jako członek społeczności?
register.curriki_intro.link2_rollover=Jako członek Curriki będziesz miał dostęp do wszystkich narzędzi edycji i tworzenia własnych materiałów edukacyjnych. Kliknij i dowiedz się więcej.
register.curriki_intro.note=&lt;b&gt;Uwaga:&lt;/b&gt; Zgodnie z Regulaminem Curriki, musisz mieć przynajmniej 13 lat aby się zarejestrować. 
register.curriki_intro.required_fields_instruction=Pola oznaczone {0} są obowiązkowe.

register.account_info.header=Wprowadź swoje dane
register.account_info.intro=Te informacje są konieczne byś mógł zostac członkiem Curriki. Odwiedź Porady aby dowiedzieć się jak zostaną one wykorzystane i dlaczego są one nam potrzebne.
register.account_info.login.title=Login użytkownika:
register.account_info.login.description=Login to Twoja unikalna nazwa. Pozwoli Ci się ona zalogować na naszej stronie. Pojawi się on również w adresach URL wszystkich materiałów, które dodasz do naszego repozytowrium.
register.account_info.login.tooltip=Login musi być unikalny. Uważaj na wielkość liter.
register.account_info.password1.title=Hasło:
register.account_info.password1.description=Wprowadź hasło, które zabezpieczy dostęp do Twojego konta. 
register.account_info.password1.tooltip=Hasło musi mieć przynajmniej 5 znaków. System uwzględnia wielkość liter.
register.account_info.password2.title=Potwierdź hasło:
register.account_info.password2.description=Wpisz ponownie wybrane przez siebie hasło.
register.account_info.password2.tooltip=Ponowne wprowadzenie hasła pozwoli Ci się upewnić, że nie popełniłeś błędu podczas wypełniania pola. Pamiętaj o wielkości liter.
register.account_info.email1.title=Adres e-mail:
register.account_info.email1.description=Podaj swój e-mail. Na ten adres wyślemy instrukcje jak zakończyć proces rejestracji.
register.account_info.email1.tooltip=Podanie adresu e-mail pozwoli nam potwierdzić Twoją tożsamość, a także ewentualnie komunikować się z Tobą w przyszłości.
register.account_info.email2.title=Potwierdź adres e-mail:
register.account_info.email2.description=Bez poprawnego adresu e-mail nie będziemy niestety mogli zakończyć rejestracji. DLatego prosimy Cię o jego potwierdzenie.
register.account_info.email2.tooltip=Jeśli nie podasz poprawnego adresu e-mail nie będziemy w stanie dokończyć Twojej rejestracji. By uniknąć pomyłek prosimy cię o jego ponowne podanie.
register.account_info.name1.title=Moja nazwa - 1:
register.account_info.name1.description=Podaj pierwszą część nazwy, która będzie widoczna dla innych członków społeczności. Znajdzie się ona w opisie przygotowanych przez Ciebie materiałów, a także ukaże się innym użytkownikom w trakcie dyskusji grupowych. Zobacz Porady by uzyskać pomoc w wpisywaniu nazw i imion.
register.account_info.name1.tooltip=Jeśli chcesz ujawnić swoje prawdziwe dane, tu możesz wpisać swoje imię. Firmy i organizacje powinny tu podać skrót swojej nazwy.
register.account_info.name2.title=Moja nazwa - 2: 
register.account_info.name2.description=Podaj drugą część nazwy, która będzie dostępna dla innych członków społeczności. Znajdzie się ona w opisie Twoich materiałów i ukaże się innym użytkownikom w trakcie dyskusji grupowych. Zobacz Porady by uzyskać pomoc w wpisywaniu nazw i imion.
register.account_info.name2.tooltip=Jeśli chcesz ujawnić swoje prawdziwe dane, tu możesz wpisać swoje nazwisko. Firmy i organizacje powinny tu podać swoją pełną nazwę.
register.account_info.error.blank_field=Część wymaganych pól nie została uzupełniona; zobacz podświetlone obszary.
register.account_info.error.login_not_unique=Niestety podany przez Ciebie login użytkownika jest już zajęty. Zaproponuj inny login.
register.account_info_error.login_invalid= Login, który podałeś jest niepoprawny; Proszę usuń wszystkie przerwy i znaki specjalne.
register.account_info.error.password_short=Hasło musi zawierać przynajmniej 5 znaków.
register.account_info.error.password_mismatch=Podałeś dwa różne hasła.
register.account_info.error.password_invalid=Hasło nie może zawierać spacji, popraw je.
register.account_info.error.email_invalid=Wpisz poprawny adres e-mail.
register.account_info.error.email_not_unique= Adres e-mail, który podałeś, został już zajęty przez innego użytkownika. Podaj inny.
register.account_info.error.email_mismatch= Podałeś dwa różne adresy e-mail.
register.account_info.error.name1_long= Moja nazwa - 1 nie może być dłuższa niż 32 znaki.
register.account_info.error.name2_long= Moja nazwa - 2 nie może być dłuższa niż 32 znaki. 

register.module.expanded=Kliknij, aby ukryć pola
register.module.collapsed=Kliknij, aby rozwinąć pola

register.privacy.header=Uzupełnij swoje ustawienia prywatności (opcjonalne)
register.privacy.intro=Określ które z Twoich danych będą dostępne dla innych użytkowników Curriki, a także jakie informacje chcesz od nas otrzymywać.
register.privacy.show_profile.title=Pokazuj mój profil:
register.privacy.show_profile.tooltip=Poniższe opcje pozwolą Ci określić którzy członkowie społeczności będą mogli oglądać Twój profil.
register.privacy.show_profile.everyone=każdemu odwiedzającemu stronę
register.privacy.show_profile.members=tylko zarejestrowanym użytkownikom Curriki 
register.privacy.show_profile.noone=nie pokazuj nikomu!

register.privacy.show_email.title=Udostępnij mój adres e-mail:
register.privacy.show_email.tooltip=Nawet jeśli chcesz udostępnić swój profil, możesz jednocześnie ukryć przed osobami postronnymi swój adres e-mail.
register.privacy.show_email.everyone=każdemu 
register.privacy.show_email.members=tylko zarejestrowanym użytkownikom Curriki 
register.privacy.show_email.noone=nie udostępniaj nikomu!
register.privacy.email_options.title=Opcje e-mail
register.privacy.email_options.tooltip=Określ jakie informacje chciałbyś od nas otrzymywać przez e-mail.
register.privacy.email_options.all=Chcę otrzymywać informacje o wszystkim co się dzieje w społeczności Curriki.
register.privacy.email_options.some=Chcę otrzymywać wiadomości o wydarzeniach tylko w wybranych, interesujących mnie tematach:
register.privacy.email_options.newsletters=Newsletter (okólnik) i ogłoszenia.
register.privacy.email_options.recommendations=Informuj mnie o ciekawych materiałach w dziedzinach którymi się interesuje (Ta opcja na razie jest niedostepna, jednak jej zaznaczneie spowoduje, że w przyszłości nie przegapisz żadnej z naszych rekomendacji).
register.privacy.email_options.reviews= Wyślij mi informacje, kiedy dodane przeze mnie materiały zostaną ocenione przez Curriki.
register.privacy.email_options.friend=Chcę uczestniczyć w sondach, które pomogą ulepszyć jakość usług.
register.privacy.email_options.none=Sam będę sprawdzał co się dzieje na stronie Curriki. Nie chcę otrzymywać żadnych wiadomości e-mail na ten temat.
register.privacy.error.no_suboptions_checked=Nie określiłeś swoich ustawień prywatności.

register.interests.header=Uzupełnij informacje o swoich zainteresowaniach (opcjonalne).
register.interests.intro=Informacje w tym dziale pomogą innym użytkownikom odnaleźć Cię i zaprosić do właściwych grup, lub do współpracy nad ciekawymi projektami. W przyszłości, w oparciu o te informacje Curriki będzie mogła informować Cię o nowościach w interesujących Cię dziedzinach. 
register.interests.member_type.title=Jestem:
register.interests.member_type.tooltip=Określ swoje powiązania z procesem nauczania. 
register.interests.affiliation.title=Czy współpracujesz z jakąś organizacją?
register.interests.affiliation.tooltip=Wymień organizacje i firmy zajmujące się problematyką edukacji w których działasz.
register.interests.subject.title=Jaką tematyką się interesujesz?
register.interests.subject.description=Kliknij na strzałki aby rozwinąć, lub zwinąć listę tematów (możesz zaznaczyć dowolną ilość).
register.interests.subject.tooltip=Poniżej znajduje się lista dziedzin według której są pogrupowane materiały na Curriki.
register.interests.ed_level.title=Jakim poziomem nauczania się interesujesz?
register.interests.ed_level.description=Wybierz jeden, lub kilka poziomów.
register.interests.ed_level.tooltip=Jeśli to możliwe określ poziom nauczania, który jest najbliższy Twoim zainteresowaniom.
register.interests.location.title=Gdzie mieszkasz?
register.interests.location.tooltip=Określenie rejonu zamieszkania pozwoli Ci poznać nauczycieli z Twojej okolicy o podobnych zainteresowaniach jak Twoje.
register.interests.country.title=Kraj:
register.interests.state.title=Województwo:
register.interests.city.title=Miasto:
register.interests.language.title=Jakim językiem się posługujesz?
register.interests.language.tooltip=Określ język, którym posługujesz się płynnie i w którym chciałbyś się komunikować poprzez Curriki.

register.terms.header=Zapisz dane i zarejestruj mnie
register.terms.intro=Zanim zakończysz upewnij się, że przeczytałeś i rozumiesz Politykę Prywatności oraz Regulamin Użytkownika Curriki.
register.terms.agreement=Akceptuję Politykę Prywatności i z Regulamin Użytkownika Curriki.
register.terms.notes=Jak tylko klikniesz na &lt;b&gt;Zapisz dane i zarejestruj mnie&lt;/b&gt; wyślemy na podany przez Ciebie adres e-mail potwierdzenie rejestracji. Postępuj zgodnie z podanymi tam wskazówkami abyśmy mogli zweryfikować Twoje dane. Następnie zaloguj się i korzystaj z materiałów udostępnionych na Curriki.&lt;br&gt;&lt;br&gt;Jeśli zaproszono Cie wcześniej do grupy, musisz najpierw zakończyć proces rejestracji, a następnie powrócić do zaproszenia. Dopiero wtedy będziesz mógł się przyłączyć.
register.terms.save_button=Zapisz dane i zarejestruj mnie.
register.terms.no_agreement=Aby zakończyć rejestrację musisz zaakceptować Politykę Prywatności i Regulaminem Użytkownika Curriki.

##END NEW REGISTRATION KEYS

# Registration pages information
registration.email=webmaster@curriki.org
registration.validate.validated=Zakończyłeś process rejestracji! Dziękujemy za zweryfikowanie swojego adresu e-mail i witamy w naszej społeczności. Wpisz swój login i hasło w odpowiednich polach na górze strony i zaloguj się.&lt;br /&gt;
registration.login_button=OK
registration.validate.failed.bad_key_or_loginname=Niestety podałeś zły login, lub hasło. Spróbuj ponownie. registration.validate.failed.no_info=Nie podałeś wszystkich wymaganych informacji.

# Registration.EmailNotReceived
email_not_received.title=Nie otrzymałem wiadomości e-mail.
email_not_received.info=Proszę podaj odpowiednie informacje poniżej a następnie kliknij WYŚLIJ.
email_not_received.required=Pola oznaczone wykrzyknikiem (!) są wymagane.
email_not_received.login=Login użytkownika:
email_not_received.email=Adres e-mail:
email_not_received.phone=Numer telefonu:
email_not_received.client=Klient poczty elektronicznej (np. Outlook)
email_not_received.button=WYŚLIJ
email_not_received.missing=Wpisz wszystkie wymagane informacje.
email_not_received.error.need_login=Podaj login.
email_not_received.error.need_email=Podaj e-mail.
email_not_received.error.need_phone=Podaj swój numer telefonu.

email_not_received.email_sent=Dziękujemy za informację. Przedstawiciel Curriki skontaktuje się z Tobą w najbliższym czasie&lt;br /&gt;&lt;br /&gt;&lt;a href="/xwiki/bin/view/Main/" class="button button-orange"&gt;Dalej&lt;/a&gt;
email_not_received_sent.title=Wysłano informację o nieotrzymanej poczcie e-mail.

#Header
loginUsername=login użytkownika

#Enter Login Name Again
username=login użytkownika

## Email Validation
emailValidation.step1.errNoUser=Nie podano loginu.
emailValidation.step1.errNotFound=Nie znaleziono użytkownika.
emailValidation.step1.errNotBounced=Adres e-mail nie odpowiada.
emailValidation.step1.errNoEmail=Adres e-mail nie odpowiada.
emailValidation.step1.errBadEmail=Wpisz poprawny adres e-mail.
emailValidation.step1.errDupEmail=Ten adres e-mail jest już zajęty przez innego użytkownika.

emailValidation.step1.title=Weryfikacja adresu e-mail – Krok 1 z 2
emailValidation.step1.intro=Bezpieczeństwo naszych użytkowników jest przedmiotem naszej szczególnej troski. Aby zapewnić im wszystkim bezpieczeństwo weryfikujemy poszczególne adresy e-mail.
emailValidation.step1.weHave=Dla {0} {1} nasza baza danych pokazuje {2}.
emailValidation.step1.pleaseSubmit=Kliknij na przycisk Wyślij aby wysłać wiadomość weryfikującą na ten adres e-mail, lub uzupełnij pole adresu e-mail (automatycznie uzupełnimy Twój profil) i następnie kliknij przycisk Wyślij.
emailValidation.step1.submit=Wyślij

emailValidation.step1.goElsewhere=Wszystkie zmiany na tej stronie zostaną utracone. Wybierz Dalej aby się wylogować i powrócić na naszą stronę główną. Możesz powrócić do tego formularza gdy zalogujesz się powtórnie. Wybierz Cofnij aby powrócić do formularza teraz i dokonać zmian.

emailValidation.sent.title=Wysłano e-mail weryfikacyjny
emailValidation.sent.text=&lt;p&gt;To był pierwszy krok weryfikacji adresu e-mail&lt;/p&gt;&lt;p&gt;Aby przejść do kroku drugiego sprawdź czy masz już w swojej skrzynce pocztowej wiadomość od webmaster@curriki.org&lt;/p&gt;&lt;p&gt;Jeśli otrzymałeś od nas żadnej wiadomości może to być wina filtru SPAM. Sprawdź swój folder SPAM i pamiętaj dodać curriki.org do listy bezpiecznych nadawców.&lt;/p&gt;
emailValidation.sent.notreceived=Jeśli nie otrzymałeś od nas żadnej wiadomości w ciągu 10 minut kliknij na formularz&lt;a href="/xwiki/bin/view/Registration/EmailNotReceived"&gt; Nie otrzymałem wiadomości e-mail&lt;/a&gt;.

emailValidation.step2.title=Zweryfikowano e-mail
emailValidation.step2.text=&lt;p&gt;Potwierdziliśmy Twój adres e-mail.&lt;/p&gt;&lt;p&gt;Dziękujemy&lt;/p&gt;
emailValidation.step2.ok=Dalej

emailValidation.step2.errNoUser=Niewłaściwy login.
emailValidation.step2.errNoVkey=Niewłaściwe hasło.
emailValidation.step2.errBadVkey=Niewłaściwe hasło.
emailValidation.step2.errNotFound=Nie znaleziono użytkownika.
emailValidation.step2.errNotBounced=E-mail nie odpowiada.


#SEND TO A FRIEND DIALOG

staf.dialog.title=Powiadom znajomego.
staf.dialog.recipients=Komu chcesz wysłać powiadomienie?:
staf.dialog.recipients.comments=Oddziel poszczególne adresy e-mail przecinkiem.
staf.dialog.customize=Dostosuj wiadomość (opcjonalne):
staf.dialog.customize.default=Te materiały znalazłem na Curriki (globalnym repozytorium materiałów edukacyjnych) i myślę, że mogą Ci się przydać.
staf.dialog.yourname=Twoje imię:
staf.dialog.ccme=Wyślij jedną kopię do mnie
staf.dialog.youremail=Twój adres e-mail:
staf.dialog.privacy=Twój e-mail jest wykorzystywany tylko po to by Twoi znajomi wiedzieli od kogo jest ta wiadomość. Podany tu adres e-mail nie zostanie przez nas zapisany ani wykorzystany przez nas, lub kogokolwiek innego w jakimkolwiek innym celu. Przejrzyj &lt;a href="/xwiki/bin/view/Main/PrivacyPolicy"&gt;Politykę Prywatności&lt;/a&gt; jeśli potrzebujesz dokładniejszych informacji.
staf.dialog.cancel=Anuluj
staf.dialog.send=Wyślij
staf.dialog.shouldcancel=Wprowadzone informacje zostaną utracone. Czy na pewno chcesz opuścić tę stronę?

staf.from=sendtoafriend@curriki.org

staf.sent.okaymsg=Wysłano e-mail.
staf.err.emptyto=Proszę wprowadź adres e-mail.
staf.err.invalidemail=Wprowadź poprawny adres e-mail.
staf.err.isguest=Tylko członkowie Curriki mogą przesyłać materiały do znajomych. Zaloguj się lub dołącz, jeśli jeszcze nie jesteś członkiem społeczności.
staf.sent.errored=Wystąpił nieznany błąd.

##AFFILIATE REGISTRATION ERROR MESSAGES
partnerrigestration.validkey.invalid=Nie byliśmy w stanie potwierdzić kodu Twojego partnera. 

partnerrigestration.parnter.notexist=Załączony Partner nie funkcjonuje w naszej bazie danych. Skontaktuj się z administratorem strony.


#OLD REGISTRATION/PROFILE TRANS KEYS TO BE REMOVED AFTER 1.8.5 LOGIN RELEASE

#Main.JoinCurriki

joincurriki.first_name.text=Wybierz nazwę, która pozwoli Cię rozpoznać innym członkom społeczności Curriki (Np. Imię, Firma, Miejesce)
joincurriki.last_name.text=Wybierz drugą nazwę, aby by, aby Choose another name to make your name unique (e.g., Last Name, Company, Location).



joincurriki.fileds.xwikiname=Login użytkownika
joincurriki.xwikiname.badchars=Ten login jest niepoprawny. Podaj login bez przerw i znaków specjalnych. Pamiętaj, że wielkość liter też zostanie uwzględniona przez system.

joincurriki.registerToJoinCurriki=Zarejestruj się, aby dołączyć do społeczności Curriki
joincurriki.infos=Dołączyłeś do społeczności Curriki. Dziękujemy za Twoje zaangażowanie. Dołączenie do Curriki to  Joining Curriki is your first step towards accessing and creating high quality teaching resources.&lt;br /&gt;&lt;br /&gt;&lt;strong&gt;Please note:&lt;/strong&gt; As per Curriki's &lt;a href="javascript:void()" onclick="showpopup('/xwiki/bin/view/Main/TOS?xpage=popup'); return false;"&gt;Terms of Service&lt;/a&gt;, you must be at least 13 years of age to register. No persons under the age of 13 may register.

joincurriki.letsKnowWhoYouAre=Opisz nam siebie
joincurriki.enterRequestedInfo=Wprowadź dane wymagane by stać się członkiem społeczności i stworzyć swój profil. Pola oznaczone wykrzyknikiem (!) są obowiązkowe.

joincurriki.enterPassword=Wprowadź swoje hasło. Uważaj na wielkość liter.
joincurriki.enterPasswordConfirm=Potwierdź swoje hasło. Pamiętaj o małych i wielkich literach.
joincurriki.enterValidEmail=Proszę wprowadź poprawny adres e-mail. Na ten adres zostaną wysłane wskazówki jak zakończyć proces rejestracji.
joincurriki.ifApplicable=(jeśli to Ciebie dotyczy)
joincurriki.showContact=To pozwala innym członkom społeczności Curriki przejrzeć Twoje osobiste dane.
joincurriki.fileds.age=Wiek
joincurriki.iAm13YearsOld=Mam przynajmniej 13 lat.
joincurriki.privacyAndTOS=Polityka Prywatności i Regulamin Użytkownika
joincurriki.privacyAndTOS.read.part1=Przeczytałem/przeczytałam, rozumiem i akceptuję
joincurriki.privacyAndTOS.read.part2=i
joincurriki.iAgree=Akceptuję
joincurriki.emailWillBeSent=Jak tylko klikniesz na przycisk "Zapisz dane i zarejestruj mnie", wyślemy e-mail z potwierdzeniem na podany powyżej adres.

joincurriki.password.noSpaces=Hasło nie może zawierać przerw.
joincurriki.password.tooShort=Twoje hasło jest zbyt krótkie. Musi zawierać przynajmniej 5 znaków.
joincurriki.password.noMatch=Podałeś dwa różne hasła.
joincurriki.email.invalidFormat=Format adresu e-mail jest niepoprawny.
joincurriki.someDataMissing=Nie wypełniłeś wszystkich pól. Zobacz podświetlone pola.
joincurriki.email.alreadyUsed=Twój adres e-mail jest już używany.
joincurriki.login.alreadyUsed=Podany przez Ciebie login jest używany przez innego użytkownika.
joincurriki.saveMyInfoAndRegister=Zapisz dane i zarejestruj mnie

joincurriki.chooseOptOut=Nie chcę otrzymywac wiadomości od Curriki na mój adres e-mail.
joincurriki.topics=

joincurriki.fields.xwikiname=Login użytkownika
joincurriki.chooseYourCommunityName=Login pozwoli Ci się zalogować na Twoje konto. Uważaj na wielkość liter.
joincurriki.fields.age=Age
joincurriki.fields.first_name=Moja nazwa  - 1:
joincurriki.fields.last_name=Moja nazwa - 2:
joincurriki.fields.password=Hasło:
joincurriki.fields.password2=Hasło (powtórz):
joincurriki.fields.email=Adres e-mail:
joincurriki.fields.opt_out=Zrezygnuj z otrzymywania wiadomości:
joincurriki.fields.topics=Tematy w obszarze mych zainteresowań:
joincurriki.fields.member_type=:
joincurriki.fields.country=Kraj:
joincurriki.fields.state=Województwo:
joincurriki.fields.city=Miasto:
joincurriki.fields.affiliation=Działam w organizacji(ach):
joincurriki.fields.show_contact=Pokazuj mój profil:

#XWiki.XWikiUserSheet

profile.edit=Edytuj profil
profile.accessDenied=Musisz być zalogowany aby móc przeglądać profile użytkowników.
profile.userNotVisible=Ten użytkownik nie udostępnia swojego profilu.
profile.removePhoto.confirmation=Czy na pewno chcesz usunąć to zdjęcie?
profile.removePhoto=Usuń zdjęcie
profile.removeYourPhoto=Usuń swoje zdjęcie
profile.modifyYourPhoto=Zmień swoje zdjęcie
profile.changeMyPhoto=Zmień moje zdjęcie
profile.changeMyPhoto.needToRemove=Musisz najpierw usunąć swoje obecne zdjęcie. Czy chcesz to teraz zrobić?
profile.field.firstName=Moja nazwa - 1:
profile.field.lastName=Moja nazwa - 2:
profile.field.display.firstName=Moja nazwa - 1:
profile.field.display.lastName=Moja nazwa - 2:
profile.field.memberType=Jestem:
profile.field.affiliation=Działam w organizacji(ach):
profile.field.country=Kraj:
profile.field.state=Województwo/Prowincja:
profile.field.city=Miasto:
profile.field.showContact=Pozwól innym członkom społeczności Curriki przeglądać mój profil:
profile.field.email=E-mail:
profile.field.opt_out=Wyklucz mnie z listy mailingowej:
profile.field.opt_out.checkbox_text=Nie chcę otrzymywać wiadomości o nowościach na Curriki.
profile.field.password=Hasło:
profile.field.passwordConfirm=Potwierdź hasło:
profile.field.topics=Dziedziny, które mnie interesują:
profile.field.bio=Biografia:
profile.emptyField=&amp;ndash;

profile.chooseAPhoto=Wybierz Zdjęcie:

profile.field.password.nospaces=Hasło nie może zawierać przerw.
profile.field.password.tooShort=Hasło jest zbyt krótkie.
profile.field.password.mustMatch=Password does not match.
profile.field.firstName.mandatory=Podanie Mojej nazwy - 1 jest obowiązkowe.
profile.field.lastName.mandatory=Podanie Mojej nazwy - 2 jest obowiązkowe.
profile.field.email.mandatory=Podanie adresu e-mail jest obowiązkowe.
profile.field.email.invalid=E-mail jest niepoprawny.


#OLD REGISTRATION/LOGIN KEYS TO CHECK WHETHER THEY ARE STILL NEEDED (not sure there are replacements in new keys)

joincurriki.repeat=powtórz
joincurriki.accountCreated=Utworzyliśmy Twoje konto. Możesz się zalogować. (Pamiętaj o wpisaniu poprawnej wielkości liter).
</content></xwikidoc>