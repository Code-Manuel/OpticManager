# OpticPatientManager

Gestionale per la gestione di pazienti e prescrizioni ottiche.

## Obiettivo
Digitalizzare la gestione quotidiana di uno studio optometrico: 
anagrafica pazienti, storico controlli della vista, prescrizioni di 
lenti/occhiali, ed export in PDF dei referti, sostituendo la 
gestione cartacea con un sistema semplice, affidabile e utilizzabile 
in locale, senza necessità di connessione internet o infrastrutture 
esterne.

## Funzionalità principali
- Gestione anagrafica pazienti (CRUD)
- Registrazione prescrizioni ottiche (gradazioni, tipo lente, note cliniche)
- Storico prescrizioni per paziente
- Generazione PDF del referto/prescrizione, pronto per la stampa o 
  la consegna al paziente
- Dati clinici strutturati secondo standard HL7 FHIR, per garantire 
  interoperabilità futura con altri sistemi sanitari

## Stack tecnico
- **Backend**: Java, Spring Boot
- **Database**: PostgreSQL
- **Generazione PDF**: Apache PDFBox
- **Standard dati clinici**: HL7 FHIR

## Esecuzione in locale
Applicazione pensata per l'uso offline, avviabile direttamente come 
jar eseguibile Spring Boot su un singolo PC, senza necessità di 
containerizzazione o server dedicati.

## Note
Il progetto è pensato per uso privato/offline all'interno dello studio 
optometrico. Non gestisce integrazioni con il Sistema Tessera Sanitaria 
o il Fascicolo Sanitario Elettronico, che richiederebbero accreditamento 
formale della struttura sanitaria.
