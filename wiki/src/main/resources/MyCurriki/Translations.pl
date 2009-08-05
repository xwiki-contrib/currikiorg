<?xml version="1.0" encoding="UTF-8"?>

<xwikidoc>
<web>MyCurriki</web>
<name>Translations</name>
<language>pl</language>
<defaultLanguage></defaultLanguage>
<translation>1</translation>
<parent></parent>
<creator>XWiki.mzielinski</creator>
<author>XWiki.mzielinski</author>
<customClass></customClass>
<contentAuthor>XWiki.mzielinski</contentAuthor>
<creationDate>1248819034000</creationDate>
<date>1249077836000</date>
<contentUpdateDate>1249077836000</contentUpdateDate>
<version>2.1</version>
<title></title>
<template></template>
<defaultTemplate></defaultTemplate>
<validationScript></validationScript>
<comment>Polish - my Curriki - changes #1 major</comment>
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
<name>MyCurriki.Translations</name>
<number>0</number>
<className>XWiki.TagClass</className>
<property>
<tags/>
</property>
</object>
<content>#{pre}
mycurriki.datetimeFormat=MMM-dd-yyyy - hh:mm a z
mycurriki.dateFormat=MMM-dd-yyyy
mycurriki.timeFormat=hh:mm a z

mycurriki.tab.blog.label=Blog

mycurriki.tab.blog.subtitle=Znajdź wszystkie wpisy na blogu od użytkownika {0}. Członkowie społeczności Curriki mogą prowadzić własnego bloga i komentować wpisy innych użytkowników.
mycurriki.tab.blog.noblog.subtitle=Znajdź wszystkie wpisy na blogu od użytkownika {0}. Członkowie społeczności Curriki mogą prowadzić własnego bloga i komentować wpisy innych użytkowników.
mycurriki.tab.collections.label=Zestawy

mycurriki.tab.collections.subtitle=Poniżej wymieniono wszystkie zbiory materiałów edukacyjnych. 
mycurriki.tab.contributions.label=Dodałem

mycurriki.tab.contributions.subtitle=Poniżej wymieniono wszystkie dodane przez Ciebie materiały. Możesz je segregegować według Nazwy, Ostatniej zmiany, i sposobu udostępniania. (You will only see Access for your own contributions.)
mycurriki.tab.favorites.label=Ulubione

mycurriki.tab.favorites.subtitle=Poniżej wymieniono materiały, które oznaczyłeś jako ulubione.
mycurriki.tab.groups.label=Grupy

mycurriki.tab.groups.subtitle=Poniżej wymieniono wszystkie grupy do których należysz.
mycurriki.tab.profile.label=Profil

##Do not translate these as they are page links and need to use the english names. Todd Nov 17, 2008

mycurriki.tab.blog.page=MyCurriki.Blog
mycurriki.tab.collections.page=MyCurriki.Collections
mycurriki.tab.contributions.page=MyCurriki.Contributions
mycurriki.tab.favorites.page=MyCurriki.Favorites
mycurriki.tab.groups.page=MyCurriki.Groups
mycurriki.tab.profile.page=MyCurriki.Profile


## GLOBAL TAB LINKS 
## The following links are used globally in My Curriki Tabs and Groups, Curriculum. This allows the macros to be the same in both My Curiiki and Groups. Todd Jan-20-2009

mycurriki.link.add=Dodaj
mycurriki.link.add.tooltip=Dodaj te materiały do innego folderu, lub zestawu edukacyjnego

mycurriki.link.copy=Przetwórz
mycurriki.link.copy.tooltip=Zacznij tworzenie nowych materiałów w oparciu o zawartość tej strony

mycurriki.link.editcontent=Edytuj
mycurriki.link.editcontent.tooltip=Popraw treść, lub zmień wygląd tego materiału

mycurriki.link.editinfo=Edytuj Opis
mycurriki.link.editinfo.tooltip=Zmień opis tego materiału

mycurriki.link.buildup=Rozbuduj
mycurriki.link.buildup.tooltip=Dodaj nowe materiały do tego zbioru

mycurriki.link.organize=Reorganizuj
mycurriki.link.organize.tooltip=Ustaw materiały w nowym porządku, lub usuń część z nich

mycurriki.link.delete=Usuń
mycurriki.link.delete.tooltip=Permanently delete this resource
mycurriki.link.delete.confirm=Are you sure you want to delete this resource?


##PROFILE TAB (NEW KEYS FOR 1.8.5 LOGIN RELEASE)
##VIEW PROFILE PAGE
##Selection values for the fields Subjects, Ed levels, Country, &amp; Language all use the global trans values.

view_profile.not_shared_with_anyone=Ten użytkownik nie udostępnia publicznie swojego profilu.
view_profiel.not_shared_with_visitors=Profil tego użytkownika jest dostępny tylko dla członków społeczności Curriki. Zaloguj się, lub zarejstruj jeśli jeszcze nie masz konta na Curriki.org.

view_profile.intro.percent_complete=Witaj z powrotem! Twój profil jest już w {0} kompletny.
view_profile.intro.edit_button=Edytuj Profil
view_profile.intro.public_button=Pokaż mój profil publiczny
view_profile.intro.return_button=Powrót do profilu

view_profile.account_info.header=Informacje o koncie
view_profile.account_info.name1.title=Nazwa - część pierwsza:
view_profile.account_info.name2.title=Nazwa - część druga:
view_profile.account_info.login.title=Login:
view_profile.account_info.email.title=Adres e-mail:
view_profile.account_info.member_since=Dołączył:

view_profile.privacy.header=Ustawienia prywatności
view_profile.privacy.show_profile.title=Pokazuj mój profil:
view_profile.privacy.show_profile.everyone=Każdemu odwiedzającemu stronę
view_profile.privacy.show_profile.members=Tylko członkom społeczności Curriki
view_profile.privacy.show_profile.noone=Nikomu go nie pokazuj
view_profile.privacy.show_email.title=Udostępnij mój adres e-mail:
view_profile.privacy.show_email.everyone=każdemu
view_profile.privacy.show_email.members=tylko zarejestrowanym członkom społeczności Curriki
view_profile.privacy.show_email.noone=nie udostępniaj nikomu!
view_profile.privacy.email_options.title=Opcje e-mail
view_profile.privacy.email_options.all=Otrzymuję informacje o wszystkim co się dzieje na stronie Curriki.
view_profile.privacy.email_options.some=Otrzymuję wiadomości o wydarzeniach tylko w wybranych, interesujących mnie obszarach:
view_profile.privacy.email_options.newsletters=Okresowy okólnik i ogłoszenia.
view_profile.privacy.email_options.recommendations=Przysyłaj mi informacje o ciekawych materiałach w dziedzinach którymi się interesuje (Ta opcja na razie jest niedostepna, jednak jej zaznaczneie spowoduje, że nie przegapisz żadnej z naszych rekomendacji)
view_profile.privacy.email_options.reviews=Wyślij mi informacje, kiedy dodane przeze mnie materiały zostaną ocenione w Systemie Ocen Curriki.
view_profile.privacy.email_options.friend=Chcę uczestniczyć w sondach, które pomogą ulepszyć jakość usługi.
view_profile.privacy.email_options.none=Sam sprawdzam co się dzieje na stronie Curriki. Nie chcę otrzymywac wiadości e-mail na ten temat.

view_profile.interests.no_info=Brak danych. Kliknij powyżej na przycisk "Edytuj Profil" aby zakonczyć tworzenie swojego profilu.
view_profile.interests.header=Czym się zajmuję
view_profile.interests.member_type.title=Kim jestem:
view_profile.interests.affiliation.title=Współpracuję z:
view_profile.interests.subject.title=Przedmioty, które mnie interesują:
view_profile.interests.ed_level.title=Poziomy nauczania, które mnie interesują:
view_profile.interests.city.title=Miasto:
view_profile.interests.state.title=Województwo:
view_profile.interests.country.title=Kraj:
view_profile.interests.language.title=Pierwszy język:
view_profile.interests.bio.title=Krótka biografia:
view_profile.interests.contact.title=Możliwości kontaktu:
view_profile.interests.websites.title=Strony internetowe/Blogi:


##EDIT PROFILE PAGE

edit_profile.change_photo.header=Zmień zdjęcie
edit_profile.change_photo.choose_file.title=Wybierz zdjęcie z komputera:
edit_profile.change_photo.choose_file.description=Aby uzyskać wskazówki dotyczące wielkości zdjęcia, kliknij na Porady. Jeśli chcesz powrócić do domyślnego wizerunku użytkownika, {click here} a usuniesz swoje obecne zdjęcie i w jego miejsce pojawi się domyślny obrazek.
edit_profile.change_photo.attach_button=Załącz ten plik
edit_profile.change_photo.remove_confirmation=Czy na pewno chcesz usunąć swoje obecne zdjęcie?

edit_profile.account_info.header=Uzupełnij dane na swoim koncie.
edit_profile.account_info.intro=Zobacz Porady, aby dowiedzieć się jak są wykorzystywane poniższe pola i dlaczego potrzebujemy tych informacji.
edit_profile.account_info.login.title=Login:
edit_profile.account_info.login.description=Tej nazwy będziesz używał aby zalogowac się na swoje konto. Będzie także wykorzystany w adresie URL wszystkich materiałów i zestawów, które przygotujesz.
edit_profile.account_info.login.tooltip=Login'y użytkowników nie mogą się powtarzać. System rozróżnia wielkość liter.
edit_profile.account_info.password1.title=Hasło:
edit_profile.account_info.password1.description=Wpisz swoje hasło
edit_profile.account_info.password1.tooltip=Hasło musi mieć przynajmniej 5 znaków. System rozróżnia wielkość liter.
edit_profile.account_info.password2.title=Potwierdź Hasło:
edit_profile.account_info.password2.description=Potwierdź swoje hasło:
edit_profile.account_info.password2.tooltip=Wpisz swoje hasło ponownie, aby upewnić się, że wpisałeś je prawidłowo.
edit_profile.account_info.email1.title=Adres e-mail:
edit_profile.account_info.email1.description=Wpisz swój adres e-mail. Dostaniesz list ze wskazówkami jak zakończyć proces rejestracji.
edit_profile.account_info.email1.tooltip=TBD
edit_profile.account_info.email2.title=Potwierdź adres e-mail:
edit_profile.account_info.email2.description=Wpisz ponownie swój adres e-mail, aby potwierdzić, że jest poprawny.
edit_profile.account_info.email2.tooltip=TBD
edit_profile.account_info.name1.title=Moja nazwa - 1
edit_profile.account_info.name1.description=Podaj pierwszą część nazwy, która będzie dostępna dla innych członków społeczności. Znajdzie się ona w opisie Twoich materiałów i ukaże się innym użytkownikom w trakcie dyskusji grupowych. Zobacz Porady by uzyskać pomoc w wpisywaniu nazw i imion.
edit_profile.account_info.name1.tooltip=Jeśli chcesz wprowadzić swoje prawdziwe nazwisko, tu powinno się znaleźć Twoje imię. Firmy i organizacje mogą tu podać skrót swojej nazwy.
edit_profile.account_info.name2.title=Moja nazwa - 2
edit_profile.account_info.name2.description=Podaj drugą część nazwy, która będzie dostępna dla innych członków społeczności. Znajdzie się ona w opisie Twoich materiałów i ukaże się innym użytkownikom w trakcie dyskusji grupowych. Zobacz Porady by uzyskać pomoc w wpisywaniu nazw i imion.
edit_profile.account_info.name2.tooltip=Jeśli chcesz wprowadzić swoje prawdziwe dane, tu możesz wpisać swoje nazwisko. Firmy i organizacje mogą tu podać swoją pełną nazwę.
edit_profile.account_info.error.blank_field=Część pól nie została uzupełniona; zobacz podświetlone obszary.
edit_profile.account_info.error.login_not_unique= Niestety podany przez Ciebie login użytkownika jest już zajęty. Zaproponuj inny login. 
edit_profile.account_info_error.login_invalid=Login, który podałeś jest niepoprawny; Proszę usuń wszystkie przerwy i znaki specjalne.
edit_profile.account_info.error.password_short=Hasło musi mieć przynajmniej 5 znaków.
edit_profile.account_info.error.password_mismatch=Podałeś dwa różne hasła.
edit_profile.account_info.error.password_invalid=Hasło nie może zawierać spacji, popraw je.
edit_profile.account_info.error.email_invalid=Wpisz poprawny adres e-mail.
edit_profile.account_info.error.email_not_unique=Adres e-mail, który podałeś, został już zajęty przez innego użytkownika. Podaj inny.
edit_profile.account_info.error.email_mismatch=Podałeś dwa różne adresy e-mail.
edit_profile.account_info.error.name1_long=Moja nazwa - 1 nie może być dłuższa niż 32 znaki.
edit_profile.account_info.error.name2_long=Moja nazwa - 2 nie może być dłuższa niż 32 znaki. 

edit_profile.privacy.header=Uzupełnij swoje ustawienia prywatności
edit_profile.privacy.intro=Określ które z Twoich danych będą dostępne dla innych użytkowników Curriki, a także jakie informacje chcesz od nas otrzymywać.
edit_profile.privacy.show_profile.title=Pokazuj mój profil:
edit_profile.privacy.show_profile.tooltip=Poniższe opcje pozwolą Ci określić którzy członkowie społeczności będą mogli oglądać Twój profil.
edit_profile.privacy.show_profile.everyone=każdemu odwiedzającemu stronę
edit_profile.privacy.show_profile.members=tylko zarejestrowanym użytkownikom Curriki
edit_profile.privacy.show_profile.noone=nie pokazuj nikomu!
edit_profile.privacy.show_email.title=Udostępnij mój adres e-mail:
edit_profile.privacy.show_email.everyone=każdemu
edit_profile.privacy.show_email.members=tylko zarejestrowanym użytkownikom Curriki
edit_profile.privacy.show_email.noone=nie udostępniaj nikomu!
edit_profile.privacy.email_options.title=Opcje e-mail
edit_profile.privacy.email_options.tooltip=Określ jakiego rodzaju informacje chciałbyś otrzymywać od nas przez e-mail
edit_profile.privacy.email_options.all=Chcę otrzymywać informacje o wszystkim co się dzieje w społeczności Curriki.
edit_profile.privacy.email_options.some=Chcę otrzymywać wiadomości o wydarzeniach tylko w wybranych, interesujących mnie obszarach:
edit_profile.privacy.email_options.newsletters=Newsletter (okólnik) i ogłoszenia.
edit_profile.privacy.email_options.recommendations=Przysyłaj mi informacje o ciekawych materiałach w tematyce którą się interesuje (Ta opcja na razie jest niedostepna, jednak jej zaznaczneie spowoduje, że w przyszłości nie przegapisz żadnej z naszych rekomendacji).
edit_profile.privacy.email_options.reviews=Wyślij mi informacje, kiedy dodane przeze mnie materiały zostaną ocenione przez Curriki.
edit_profile.privacy.email_options.friend=Chcę uczestniczyć w sondach, które pomogą ulepszyć jakość usług.
edit_profile.privacy.email_options.none= Sam będę sprawdzał co się dzieje na stronie Curriki. Nie chcę otrzymywać żadnych wiadomości e-mail na ten temat.
edit_profile.privacy.error.no_suboptions_checked=TBD

edit_profile.interests.header=Uzupełnij informacje o swoich zainteresowaniach. 
edit_profile.interests.intro=Informacje w tym dziale pomogą odnaleźć Cię innym użytkownikom i zaprosić do właściwych grup, lub współpracy nad ciekawymi projektami. W przyszłości, w oparciu o te informacje Curriki będzie mogła informować Cię o nowościach w interesującej Cię tematyce. 
edit_profile.interests.member_type.title=Jesteś:
edit_profile.interests.member_type.tooltip=TBD
edit_profile.interests.affiliation.title=Czy współpracujesz z jakąś organizacją?
edit_profile.interests.affiliation.tooltip=TBD
edit_profile.interests.subject.title=Jaką tematyką się interesujesz?
edit_profile.interests.subject.description=Kliknij na strzałki aby rozwinąć, lub zwinąć listę tematów (możesz wybrać dowolną ilość).
edit_profile.interests.subject.tooltip=TBD
edit_profile.interests.ed_level.title=Jakim poziomem nauczania się interesujesz?
edit_profile.interests.ed_level.description=Wybierz jeden, lub kilka poziomów.
edit_profile.interests.ed_level.tooltip=TBD
edit_profile.interests.location.title=Gdzie mieszkasz?
edit_profile.interests.location.tooltip=TBD
edit_profile.interests.country.title=Kraj:
edit_profile.interests.state.title=Województwo:
edit_profile.interests.city.title=Miasto:
edit_profile.interests.language.title=Jaki jest Twój pierwszy język?
edit_profile.interests.language.tooltip=TBD
edit_profile.interests.bio.title=Życiorys:
edit_profile.interests.bio.description=Na górze znajduje się pasek edycji HTML. Pomoże ci sformatować treść. Czy potrzebujesz pomocy?
edit_profile.interests.bio.tooltip=TBD
edit_profile.interests.contact.title=Jak można się ze mną skontaktować:
edit_profile.interests.contact.description=Na górze znajduje się pasek edycji HTML. Pomoże ci sformatować treść. Czy potrzebujesz pomocy?
edit_profile.interests.contact.tooltip=TBD
edit_profile.intersts.websites.title=Moje strony internetowe/blogi
edit_profile.intersts.websites.description=Na górze znajduje się pasek edycji HTML. Pomoże ci sformatować treść. Czy potrzebujesz pomocy?
edit_profile.intersts.websites.tooltip=TBD

edit_profile.cancel_button=Cofnij
edit_profile.save_button=Zapisz
edit_profile.clickaway_message=Czy jesteś pewien, że chcesz wyjść z tej strony? Wprowadzone przez Ciebie nowe dane nie zostaną zapisane. Kliknij Dalej aby kontynuować, lub Cofnij aby pozostać na stronie i kontynuować wprowadzanie zmian.



##OLD PROFILE TAB KEYS (to be removed after 1.8.5 login release)

mycurriki.profile.titlebar=Dane Użytkownika
mycurriki.profile.editButton=Zmień Profil
mycurriki.profile.needPicture=Wybierz plik (musi to być plik graficzny)

mycurriki.editprofile.titlebar=Zmień dane użytkownika
mycurriki.editprofile.saveButton=Zapisz
mycurriki.editprofile.confirmLeave=Opuszczasz formularz zmiany profilu użytkownika. Czy na pewno chcesz opuścić tę stronę? Wprowadzone przez Ciebie nowe dane nie zostaną zapisane.


##FAVORITES TAB

mycurriki.favorites.collection.title=Ulubione
mycurriki.favorites.collection.description=Materiały, które oznaczyłeś jako swoje ulubione.
mycurriki.favorites.viewButton=Zobacz wszystkie
mycurriki.favorites.table.title=Tytuł
mycurriki.favorites.table.contributor=Dodane przez
mycurriki.favorites.table.ict=ICT
mycurriki.favorites.table.filetype=Rodzaj materiałów
mycurriki.favorites.table.action=Zmienione

mycurriki.favorites.table.action.remove=Usuń
mycurriki.favorites.table.action.remove_tooltip=Usuń te materiały z listy swoich ulubionych
mycurriki.favorites.table.action.remove.confirm=Czy na pewno chcesz usunąć te materiały z listy ulubionych?
mycurriki.favorites.removed.comment=Usunięto {0} z Ulubionych

mycurriki.favorites.noresults=
mycurriki.favorites.mouseover.description=&lt;strong&gt;Opis:&lt;/strong&gt;
mycurriki.favorites.mouseover.subject=&lt;strong&gt;Przedmiot(y):&lt;/strong&gt;
mycurriki.favorites.mouseover.level=&lt;strong&gt;Poziom(y) nauczania:&lt;/strong&gt;

##CONTRIBUTIONS TAB

mycurriki.contributions.addButton=Dodaj materiały
mycurriki.contributions.addButton_tooltip=Dodaj materiały do nauczania, które uważasz za ciekawe, lub stwórz coś nowego
mycurriki.contributions.table.title=Tytuł
mycurriki.contributions.table.ict=ICT
mycurriki.contributions.table.lastupdated=Ostatnia zmiana
mycurriki.contributions.table.access=Dostęp
mycurriki.contributions.table.filetype=Rodzaj materiałów
mycurriki.contributions.table.action=Podjęte działania
mycurriki.contributions.noresults=


##COLLECTIONS TAB

mycurriki.collections.titlebar=Zestaw planów nauczania
mycurriki.collections.titlebarbutton=Usuń opisy
mycurriki.collections.titlebarbuttonalt=Pokaż opisy
mycurriki.collections.addButton=Dodaj zestaw
mycurriki.collections.addButton_tooltip=Dodaj nowy zestaw materiałów
mycurriki.collections.noresults=



##REORDER FEATURE

mycurriki.collections.reorder.link=Zmień układ

mycurriki.collections.reorder.checkfirst.dialog=Domyślnie, twoje zbiory są zorganizowane tak, że na górze listy są umieszczone ostatnio zmodyfikowane materiały. Jeśli chcesz by było one uporządkowane w inny sposób, kliknij dalej.
mycurriki.collections.reorder.checkfirst.cancel.btt=Dalej
mycurriki.collections.reorder.checkfirst.next.btt=Cofnij

mycurriki.collections.reorder.dialog_title=Zmień układ swojej kolekcji
mycurriki.collections.reorder.guidingquestion=W jakim porządku mają byc prezentowane Twoje kolekcje planów nauczania?
mycurriki.collections.reorder.instruction=Uporządkuj swoją bibliotekę przeciągając poszczególne materiały w odpowiednie miejsce. Następnie kliknij Dalej.
mycurriki.collections.reorder.listheader=
mycurriki.collections.reorder.cancel.btt=Cofnij
mycurriki.collections.reorder.next.btt=Dalej

mycurriki.collections.reorder.error=Podzczas gdy zmieniałeś układ swojej kolekcji, dodano nową kolekcje, lub zreorganizowano układ Twoich kolekcji. Ponieważ te zmiany nastąpiły w trakcie Twojej pracy, zostały automatycznie zapisane przez system. Nie możemy zapisac Twoich ostatnich zmian. (Możesz ponownie klinąć na Porządkuj kolekcje i ponowić czynność)
mycurriki.collections.reorder.error.ok.btt=OK

mycurriki.collections.reorder.set.confirm=Zapisano nowy układ
mycurriki.collections.reorder.set.confirm.btt=OK

mycurriki.collections.reorder.checkafter.dialog=Już raz zmieniłeś(aś) układ swojej kolekcji, czy chcesz uczynić to ponownie? Kliknik Dalej aby kontynuować.
mycurriki.collections.reorder.checkafter.cancel.btt=Dalej
mycurriki.collections.reorder.checkafter.next.btt=Cofnij

##BLOG TAB

mycurriki.blog.titlebar=Wpisy na moim blogu
mycurriki.blog.createEntry=Dodaj wpis
mycurriki.blog.createBlog=Stwórz Blog
mycurriki.blog.noblog=
mycurriki.blog.postedBy={0} napisał {1} o {2}
mycurriki.blog.comment.delete=Skasuj
mycurriki.blog.comment.delete.confirm=Czy chcesz skasowac ten komentarz?
mycurriki.blog.comment.empty=Wprowadź komentarz, który chcesz zapisać.
mycurriki.blog.actions.delete.confirm=Czy na pewno chcesz skasować ten wpis na swoim blogu?

##GROUPS TAB

mycurriki.groups.visit=Odwiedź tę grupę
mycurriki.groups.mymessages=Moje najświeższe wiadomości
mycurriki.groups.viewmessages=Zobacz wiadomości dla grupy &amp;raquo;
mycurriki.groups.message.by=przez

mycurriki.macro.filetype.URL=URL
mycurriki.macro.filetype.unknown=Nieznany
mycurriki.macro.filetype.curriki=Strona Curriki
mycurriki.macro.filetype.currikulum=Materiały do nauczania

mycurriki.macro.access.public=Publiczny

mycurriki.macro.access.members=Chroniony
mycurriki.macro.access.private=Prywatny

mycurriki.macro.paginate.results=Rezultaty
mycurriki.macro.paginate.of=of about
mycurriki.macro.paginate.previous=Poprzedni
mycurriki.macro.paginate.next=Następny
#{/pre}
</content></xwikidoc>