## Task 1:
### Add secrets and activate actions on GitHub repo.
    These secrets and env varibles needs to be set up in github action:
    - AWS_ACCESS_KEY_ID
        <ID from IAM>
    - AWS_SECRET_ACCESS_KEY
        <Key from IAM>
    - CANDIDATE_NUMBER (works as prefix in the code)
        2016
    - EMAIL_ADDRESS
        <email adress to send alarm notification>

    - Settings -> Actions -> General -> Allow all actions and reusable workflows -> save
### Bucket needed to store template.yml, needs to be created manually if AWS env is reset:
    - lambda-deployments-2016

## Task 2:
### Need to create ecr repo manually AWS env is reset
    - 2016-repo

## Task 4:

### Metrics context
    - Monitoring on a construction site.
        - Scanning
            - Camera scanning people entering and exiting the area.
            - Camera scanning if people are using Required PPE equipment.
            - Camera scanning people for weapons.
        - Seven Metrics in dashboard
            - To know how many people getting scanned for PPE equipment.
            - To know how many people violating PPE requirements.
            - To know how many people getting scanned for weapons.
            - To know how many weapons was discovered.
            - Gauge to show how many people are in the construction site incase of emergency etc.
            - Two timers tracking the time it take for doing a PPE scan and a Weapon scan. 
              With these timers we can together with the other factors, how many persons scanned and how many violations, 
              try to optimize/improve the scanning routine. An example can be if we find out that the scanning drasticly goes up
              if there are more then 10 people in one scan, then we might want to find a way to split the input up in two parts before scanning.
		- Alarm
			- I chose a alarm to go off if a weapon is detected, this way the security personel can react in a proper manner.
			- I could set an alarm if a violation in the PPE requirement aswell, but this is kinda duplicate the same thing
			  and also i dont want an email everytime somebody dont wear the required equipment. 


## Task 5
### A)
    Definisjon: 
		Den automatiske prosessen som gjør at flere kodere som jobber på samme prosjekt, lett kan integrere koden sin mot main branchen.
	Fordel: 
		- Mulighet for å sette opp automatiserte tester som pusha kode må passere for å bli integrert mot branchen.
		Dette forbedrer kodekvaliteten da du er sikker på at all kode som blir integrert i branchen må ha passert testene.
		Som utvikler er det mye mer effektivt da arbeidsprosessen din ikke er avhengig av at testere må være tilgjengelig, før du får pushe ny kode. 
		- Mulighet for å sette opp "branch protection" slik at koden for eksempel må godkjennes av en annen person i prosjektet eller evnt komme med forbedrings forslag.
		Nok et eksempel på at koden på main branchen har bedre kodekvalitet da brancher ikke blir pulla inn til main, uten at det er sjekket av minst 1 annen person i prosjektet
		Her har du også mulighet for å lære nye ting og forbedre koden da personer kan komme med krav, forbedrings forslag og kommentarer når en pull request blir sendt.
		Selv om det muligens er "feil" mindset, så gir dette også en trygghet at noen kan snappe opp i feil/mangler som du gjør slik at du da kan kode med mer selvsikkerhet, dog mer effektivt.
		- Mulighet for å kjøre infrastruktur som kode automatisk. Dette åpner for muligheten om at noen kan lage ferdige moduler av hvordan infrastrukturen skal se ut slik at ikke alle utviklerene
		trenger å tenke på hvordan dette gjøres og hvordan det skal være utformet. Dette gjør at utvikleren kan konsentrere seg om videreutvikling og ikke trenger å repetere den samme infrastrukturen igjen og igjen.
		I tillegg har de som jobber med prosjektet en trygghet på at alle som jobber med samme infrastruktur.
		Alt dette gjør det lettere å sette opp forskjellige miljøer som er helt like, men at en kan brukes i dev og en i prod for eksempel. 
        Videre vil dette resultere i å kunne slippe ny funksjonalitet fortere da du kan være mer sikker på at om det virker i dev, så virker det i prod. Sannsynligheten er også betraktelig høyere (umulig å teste for             alle feil) for at en feil blir håndtert med en gang og slipper da å rulle tilbake mange versjoner.
		
	Jobbing:
		Jeg har egentlig forklart hvordan det brukes mot github og github actions ovenfor, men for å utfylle det jeg allerede har skrevet:
		Jeg tar utgangspunkt i at utviklerene har laget ferdig modul med infrastruktur, tester og blitt enige om hvordan branch protection skal settes opp.
		Det settes opp en repo med en hovedbranch(main) som da inneholder den siste fullstendige og fungerende koden. Når ny funksjonalitet evnt endringer skal testes, lages en 
		ny branch som det da jobbes på. Når jeg som utvikler da pusher ny kode til branchen vil github action automatisk kjøre koden igjennom testene. 
        Når den nye funksjonaliteten (branchen) er ferdig, gjøres det en forespørsel om å pulle branchen inn til hoved bracnhen (main). 
        Alt etter hvilken branch protection utviklerene har satt opp vil denne forespørselen stå åpen helt til kravene er infridd, da vil github actions automatisk kjøre eventuelle 
		tester for så å merge branchen inn til main. Om utviklerne også har satt opp actions til å bygge og deploye prosjektet på hver pull request til main, blir dette også gjort automatisk.
		For eksempel, bygge image -> kjøre tf kode som setter opp repo -> deploye imaget til ecr.


### B)
	1)
    	Scrum:
    	
		Lage userstories i sammerbeid med kunden -> dele userstories opp i oppgaver -> tilegnde vanskelighetsgrad (story points) -> kjøre sprint på 1 - 4 uker der oppgaver grupper mener de kan gjennomføre settes inn ->
    	sprinten evalueres underveis med daily standup og i slutten med sprint review (gruppen) og sprint retrospect (scrum master og kunden).
    	
    	I grove trekk vil jeg si det handler om å dele et prosjekt opp i mindre og mindre biter slik at en gruppe lettere kan organisere seg.
    	Dette gjør at en gruppe får bedre oversikt over fremgang, hvilke oppgaver så må gjøres og evnt hvilke oppgaver som må velges vekk pga tid.
    	Et annet viktig element er at kunden raskere kan få en forståelse om hva utviklerene har laget slik at det hele tiden kan tilpasses/forbedres slik at det passer hva kunden ønsker seg.
    	
    	For meg som utvikler vil jeg si fordelene ligger:
    	Standup gjør at jeg kan si ifra om jeg sitter fast og kan da få hjelp slik at prosjektet går videre.
    	Valg av story gjør at jeg kan konsentrere meg om en isolert oppgave.
    	Oppdeling i mindre oppgaver gjør at jeg oftere kjenner på fremgang/mestring (dra fra doing til done).
    	Kontinuerlig komunikasjon gjør meg tryggere på at vi som gruppe beveger oss i riktig retning.
    	
    	Det første jeg tenker på som ulempe er jo også en fordel, og det er møtene. Det går mye tid på møter å du dras inn og ut av "flowen".
    	Jeg tenker også at siden komunikasjon og sammarbeid er så vesentlig med å jobbe i scrum, så finnes det nok mange som syntes at det er mer utfordrende enn behjelpelig for fremmgangen sin.
    	Jeg er avhenging av noen som har god erfaring med scrum for å "guide", da det er så sykt mye greier rundt den faktiske utviklingen.
    		
	2)
    	DevOps:
        Alle disse prinsippene blir beskrevet i mer detalj ellers i oppgaven, men som en oversikt: 
    	    Flyt -> få bedre "flyt" i kodeprosessen med automatiske rutiner(CI, CD, IAC,PIPELINE). 
    	    Feedback -> bruke verktøy som logging, alarm, metrikker og tester for å få tilbakemeldinger om faktorer i prosjektet. 
    	    Kontinuerlig forbedring -> Bruke data fra "feedback" til å hele tiden forbedre produktet. Dette kan være bekreftelse på om en feature er velykket, som vist i oppgave C, eller en log som viser til
    	    hvem/hva som har forårsaket en feil. Blameless postmortem er et konsept satt opp for å håndtere feedback om en feil slik at fokuset er på å forbedre prosessen heller en å gi noen skylda.
    
    	Siden DevOps er basert på å sette sammen avdelingene utvikling og drift, heller enn å ha de splittet, åpner dette for muligheten for å hele tiden forbedre kvaliteten av produktet.
    	Jeg som utvikler kan få kontinuerlig tilbakemelding om utfordringer som kommer når produktet er i produksjon, noe som ved for eksempel "fossefall" ikke ville kommet før produktet var ferdig i utviklingsfasen og 
        sendt over til drift.
    	En av de åpenlyse fordelene er at det er lettere for de som jobber med produktet å fikse i problemer heller enn at ansvaret blir blankt sendt over til en drift avdeling som ikke nødvendigvis har noe kjennskap            til utviklingsprosessen. 
        Med å bruke "flyt" metoder sikrer teamet en raskere og sikrere måte og sette opp ting som ikke nødvendigvis handler om å utvikle produktet, som resulterer i at jeg som utvikler kan konsentrere meg i høyere grad          om hva jeg har som min ekspertise. Dette resulterer igjen i bedre kodekvalitet og raskere levering av et fungerende produkt.
    	
    	Det jeg tenker på som er en utfording med denne måten å jobbe på er at hele prosjektets kvalitet avhenger av en god pipeline. Fordelen med at jeg som utvikler kan konsentrere meg om mitt felt, gjør også at jeg           ikke bruker fokus på pipeline og går utifra at dette er bra.
    	Er for eksempel testene eller de automatiske verktøyene satt opp dårlig, så kan den tryggheten jeg som utvikler føler på med å jobbe i DevOps, fort resultere i store problemer(falsk trygghet).
    
    3)
    	Jeg syntes det er litt vanskelig å sammenligne Scrum og DevOps, da jeg føler en av fordelene med scrum blant annet handler om å effektivisere en utviklings prosess som må gjøres i begge metodikkene.
    	Jeg skal forklare tankegangen med et eksempel der vi isolert ser på utviklingen av en feature:
    	si vi skal lage en feature som vi i dette eksempelet vet tar 1,5 uke 
    	-> devops bruker 1,5 uke så leverer de produktet 
    	-> scrum forbedrer utviklings prosessen slik at de klarer det på en sprint(1 uke). Dette er teamet dog avhengig av erfarne personer slik at ting som feks storypoints faktisk er reelle. 
    	-> da har scrum levert produktet hurtigere enn devops.
    	-> på den andre siden så kan en feature som tar for eksempel 2 dager bli levert med en gang den er ferdig i devOps, men blir holdt igjen til sprinten er ferdig med scrum.
    	-> da er devops mer effektivt enn scrum.
    	
    	Overordnet føler jeg at om vi er i startfasen/mindre prosjekt som kanskje er preget av og hele tiden tilpasse seg kunden sine behov og vite hvilke saker som bør prioriteres så er scrum fordelaktig. 
    	På den andre siden, om prosjektet er større og kanskje mer GRUNNLAGT og kanskje er mer avhengig av ofte og pålitlig levere oppdateringer, så vil fordelene av automatiserte prosesser være mye mer fordelaktig.
    	
    	Om jeg personlig skal grovt oppsummere , liker jeg scrum sin måte og klare å komme fra ingenting til et produkt og også litt sånn, du har ansvaret for dette mens jeg konsentrerer meg om det jeg har annsvaret for.
    	På den andre siden liker jeg kulturen og sammarbeidet om å hele tiden sammen jobbe mot å få produktet og prosessen forbedret som devOps resulterer i. 
    	Elsker konseptet med blameless som gjør at jeg ikke blir frarøvet muligheten til å forbedre meg, men samtidig ikke trenger å kjenne på skamm/dårlig samvittighet for å ha gjort en feil.  
    	
	


### C)
	Det første jeg tenker på er web butikk.
	Jeg tenker det er et godt eksempel da den har 1 klart mål, mest mulig salg, og samtidig er salget basert på utrolig mange faktorer som det kan være nyttig å "tracke" for å sjekke om en feature er bra eller ikke.
	For eksempel, denne nettsiden har bare en statisk forside med innlogging, hoved kategorier, logo og bedrift info. Videre ønsker jeg å legge til en feature som gjør forsiden mer innbydende.
	Dette kan være jeg på forsiden ønsker at brukeren blir presentert med de beste tilbudene rullerende en etter en. Jeg ønsker kanskje til og med at de tilbudene som vises skal være 
	basert på de mest solgte produktene i den regionen bruker kommer fra.
	I utviklings fasen ville jeg åpenlyst tatt i bruk logging og deretter tracing for å kunne rette opp i feil som oppstår mer effektivt.
	Videre går jeg her utifra at vi allerede har satt opp metrikker som holder oversikt på hoved faktorer som kan brukes for bedømme ting som påvirker målet, altså mer salg.
	Dette kan være for eksempel, antall besøkende, antall besøkende som går videre inn fra hovedsiden, antall besøkende som legger ting i handlekurv uten å fullføre kjøp, og selvsagt antall gjennomførte salg.
	Med disse metrikkene, visualisert med hjelp av grafer, kan jeg nå få hjelp til å avgjøre om den nye featuren min er en suksess eller ikke.
	Det kan også tenkes at om jeg har to regioner som vanligvis ligger relativt likt i salg, så kan jeg sette opp den nye featuren min i region A og kjøre region B med den vanlige forsiden (A/B Testing).	
	Dette vil da gi meg enda mer sikkerhet på dataen jeg samler fra metrikkene før vi eventuelt slipper den nye featuren for alle brukerne.
	Si nå at vi fant ut at den nye forsiden var en suksess, da kan vi prøve å forbedre den med eksempelet at tilbudene er basert på de mest solgte produktene i den regionen brukeren er fra.
	Da gjør vi prosessen igjen og tester en med vanlig rullerende tilbud og en med den nye funksjonaliteten, og kan på nytt ta en avgjørelse om den nye funksjonaliteten var en forbedring eller ikke.
	Nok en ting metrikker kan gi oss, er at vi oppdager faktorer som påvirker salget som vi kanskje ikke hadde tenkt over skulle hatt noe å bety.
	Dette er et eksempel på hvordan vi kan bruke feedback på å teste nye features, men også forbedre eksisterende features.
	
	
	
