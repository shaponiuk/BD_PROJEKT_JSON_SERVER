ALTER TABLE uslugi
DROP CONSTRAINT status_uslugi;

ALTER TABLE uslugi
DROP CONSTRAINT lekarze_uslugi;

ALTER TABLE lekarze
DROP CONSTRAINT specjalizacje_lekarze;

ALTER TABLE szablon_uslug
DROP CONSTRAINT specjalizacje_szablon_uslug;

ALTER TABLE uslugi
DROP CONSTRAINT pacjenci_uslugi;

ALTER TABLE szablon_zasobow_uslug
DROP CONSTRAINT zasoby_sz_uslug;

ALTER TABLE szablon_zasobow_uslug
DROP CONSTRAINT szablon_uslug_sz_uslug;

DROP TABLE statusy_uslug;
DROP TABLE lekarze;
DROP TABLE specjalizacje;
DROP TABLE uslugi;
DROP TABLE pacjenci;
DROP TABLE zasoby;
DROP TABLE szablon_zasobow_uslug;
DROP TABLE szablon_uslug;

CREATE TABLE statusy_uslug (
        id int PRIMARY KEY,
        nazwa varchar2(255) NOT NULL
);

CREATE TABLE lekarze (
        id int PRIMARY KEY,
        imie varchar2(255) NOT NULL,
        nazwisko varchar2(255) NOT NULL,
        specjalizacje_id int NOT NULL
);

CREATE TABLE specjalizacje (
        id int PRIMARY KEY,
        nazwa varchar2(255) NOT NULL
);

CREATE TABLE uslugi (
        id int PRIMARY KEY,
        status_id int NOT NULL,
        pacjenci_PESEL int NOT NULL,
        lekarze_id int NOT NULL,
        data date,
        szablon_uslug_id int NOT NULL
);

CREATE TABLE pacjenci (
        PESEL int PRIMARY KEY,
        imie varchar2(255) NOT NULL,
        nazwisko varchar2(255) NOT NULL
);

CREATE TABLE zasoby (
        id int PRIMARY KEY,
        nazwa varchar2(255) NOT NULL,
        stan int NOT NULL
);

CREATE TABLE szablon_zasobow_uslug (
        szablon_uslug_id int NOT NULL,
        zasoby_id int NOT NULL,
        ilosc int NOT NULL
);

CREATE TABLE szablon_uslug (
        id int PRIMARY KEY,
        specjalizacje_id int NOT NULL,
        nazwa varchar2(255) NOT NULL
);

CREATE UNIQUE INDEX statusy_uslug_id
ON statusy_uslug(id);

CREATE UNIQUE INDEX lekarze_id
ON lekarze(id);

CREATE INDEX lekarze_specjalizacje_id
ON lekarze(specjalizacje_id);

CREATE UNIQUE INDEX specjalizacje_id
ON specjalizacje(id);

CREATE UNIQUE INDEX uslugi_id
ON uslugi(id);

CREATE INDEX uslugi_doctor_id
ON uslugi(lekarze_id);

CREATE INDEX uslugi_pacjenci_pesel
ON uslugi(pacjenci_PESEL);

CREATE UNIQUE INDEX pacjenci_pesel
ON pacjenci(PESEL);

CREATE UNIQUE INDEX zasoby_id
ON zasoby(id);

CREATE INDEX szablon_zasobow_uslug_szid
ON szablon_zasobow_uslug(szablon_uslug_id);

CREATE UNIQUE INDEX szablon_uslug_id
ON szablon_uslug(id);

ALTER TABLE uslugi
ADD CONSTRAINT status_uslugi
  FOREIGN KEY (status_id)
  REFERENCES statusy_uslug (id);

ALTER TABLE uslugi
ADD CONSTRAINT lekarze_uslugi
  FOREIGN KEY (lekarze_id)
  REFERENCES lekarze (id);

ALTER TABLE lekarze
ADD CONSTRAINT specjalizacje_lekarze
  FOREIGN KEY (specjalizacje_id)
  REFERENCES specjalizacje (id);

ALTER TABLE szablon_uslug
ADD CONSTRAINT specjalizacje_szablon_uslug
  FOREIGN KEY (specjalizacje_id)
  REFERENCES specjalizacje (id);

ALTER TABLE uslugi
ADD CONSTRAINT pacjenci_uslugi
  FOREIGN KEY (pacjenci_PESEL)
  REFERENCES pacjenci (PESEL);

ALTER TABLE szablon_zasobow_uslug
ADD CONSTRAINT zasoby_sz_uslug
  FOREIGN KEY (zasoby_id)
  REFERENCES zasoby (id);

ALTER TABLE szablon_zasobow_uslug
ADD CONSTRAINT szablon_uslug_sz_uslug
  FOREIGN KEY (szablon_uslug_id)
  REFERENCES szablon_uslug (id);

CREATE OR REPLACE TRIGGER czy_spec_uslugi_w_bazie
BEFORE INSERT OR UPDATE ON szablon_uslug
FOR EACH ROW
DECLARE
        spec_count int;
BEGIN
        SELECT count(*)
        INTO spec_count
        FROM specjalizacje
        WHERE id = :NEW.specjalizacje_id;

        IF spec_count = 0 THEN
                raise_application_error(-20000, 'Nie ma w bazie specjalizacji dla tej uslugi');
        END IF;
END;
/

CREATE OR REPLACE TRIGGER czy_spec_lekarza_w_bazie
BEFORE INSERT OR UPDATE ON lekarze
FOR EACH ROW
DECLARE
        spec_count int;
BEGIN
        SELECT count(*)
        INTO spec_count
        FROM specjalizacje
        WHERE id = :NEW.specjalizacje_id;

        IF spec_count = 0 THEN
                raise_application_error(-20000, 'Nie ma w bazie specjalizacji tego lekarza');
        END IF;
END;
/

CREATE OR REPLACE TRIGGER czy_status_uslugi_w_bazie
BEFORE INSERT OR UPDATE ON uslugi
FOR EACH ROW
DECLARE
        status_count int;
BEGIN
        SELECT count(*)
        INTO status_count
        FROM statusy_uslug
        WHERE id = :NEW.status_id;

        IF status_count = 0 THEN
                raise_application_error(-20000, 'Nie ma w bazie statusu tej uslugi');
        END IF;
END;
/

CREATE OR REPLACE TRIGGER czy_lekarz_w_bazie
BEFORE INSERT OR UPDATE ON uslugi
FOR EACH ROW
DECLARE
        lekarze_count int;
BEGIN
        SELECT count(*)
        INTO lekarze_count
        FROM lekarze
        WHERE id = :NEW.lekarze_id;

        IF lekarze_count = 0 THEN
                raise_application_error(-20000, 'Nie ma w bazie takiego lekarza');
        END IF;
END;
/

CREATE OR REPLACE TRIGGER czy_pacjent_w_bazie
BEFORE INSERT OR UPDATE ON uslugi
FOR EACH ROW
DECLARE
        pacjenci_count int;
BEGIN
        SELECT count(*)
        INTO pacjenci_count
        FROM pacjenci
        WHERE PESEL = :NEW.pacjenci_PESEL;

        IF pacjenci_count = 0 THEN
                raise_application_error(-20000, 'Nie ma w bazie takiego pacjenta');
        END IF;
END;
/

CREATE OR REPLACE TRIGGER czy_uslugi_su_w_bazie
BEFORE INSERT OR UPDATE ON uslugi
FOR EACH ROW
DECLARE
        szablon_uslug_count int;
BEGIN
        SELECT count(*)
        INTO szablon_uslug_count
        FROM szablon_uslug
        WHERE id = :NEW.szablon_uslug_id;

        IF szablon_uslug_count = 0 THEN
                raise_application_error(-20000, 'Nie ma takiego szablonu uslugi w bazie');
        END IF;
END;
/

CREATE OR REPLACE TRIGGER czy_SZU_su_w_bazie
BEFORE INSERT OR UPDATE ON szablon_zasobow_uslug
FOR EACH ROW
DECLARE
        szablon_uslug_count int;
BEGIN
        SELECT count(*)
        INTO szablon_uslug_count
        FROM szablon_uslug
        WHERE id = :NEW.szablon_uslug_id;

        IF szablon_uslug_count = 0 THEN
                raise_application_error(-20000, 'Nie ma takiego szablonu uslugi w bazie');
        END IF;
END;
/

CREATE OR REPLACE TRIGGER usuwanie_statusu
BEFORE DELETE ON statusy_uslug
FOR EACH ROW
DECLARE
        cnt int;
BEGIN
        SELECT count(*)
        INTO cnt
        FROM uslugi
        WHERE status_id = :OLD.id;

        IF cnt > 0 THEN
                raise_application_error(-20000, 'Istnieje usluga o tym statusie');
        END IF;
END;
/

CREATE OR REPLACE TRIGGER usuwanie_specjalizacji
BEFORE DELETE ON specjalizacje
FOR EACH ROW
DECLARE
        cnt int;
BEGIN
        SELECT count(*)
        INTO cnt
        FROM lekarze
        WHERE specjalizacje_id = :OLD.id;

        IF cnt > 0 THEN
                raise_application_error(-20000, 'Istnieje lekarz o tej specjalizacji');
        END IF;

        SELECT count(*)
        INTO cnt
        FROM szablon_uslug
        WHERE specjalizacje_id = :OLD.id;

        IF cnt > 0 THEN
                raise_application_error(-20000, 'Istnieje szablon uslugi z tą specjalizacją');
        END IF;
END;
/

CREATE OR REPLACE TRIGGER usuwanie_pacjenta
BEFORE DELETE ON pacjenci
FOR EACH ROW
DECLARE
        cnt int;
BEGIN
        SELECT count(*)
        INTO cnt
        FROM uslugi
        WHERE pacjenci_PESEL = :OLD.PESEL;

        IF cnt > 0 THEN
                raise_application_error(-20000, 'Istnieje usluga dla tego pacjenta');
        END IF;
END;
/

CREATE OR REPLACE TRIGGER usuwanie_zasobu
BEFORE DELETE ON zasoby
FOR EACH ROW
DECLARE
        cnt int;
BEGIN
        SELECT count(*)
        INTO cnt
        FROM szablon_zasobow_uslug
        WHERE zasoby_id = :OLD.id;

        IF cnt > 0 THEN
                raise_application_error(-20000, 'Istnieje szablon usługi dla tego zasobu');
        END IF;
END;
/

CREATE OR REPLACE TRIGGER usuwanie_szablonu_uslugi
BEFORE DELETE ON szablon_uslug
FOR EACH ROW
DECLARE
        cnt int;
BEGIN
        SELECT count(*)
        INTO cnt
        FROM uslugi
        WHERE szablon_uslug_id = :OLD.id;

        IF cnt > 0 THEN
                raise_application_error(-20000, 'Istnieje usluga o tym szablonie');
        END IF;

        SELECT count(*)
        INTO cnt
        FROM szablon_zasobow_uslug
        WHERE szablon_uslug_id = :OLD.id;

        IF cnt > 0 THEN
                raise_application_error(-20000, 'Istnieje szablon zasobów usługi dla tego szablonu');
        END IF;
END;
/

CREATE OR REPLACE TRIGGER stan_zasobu_nieujemny
BEFORE INSERT OR UPDATE ON zasoby
FOR EACH ROW
BEGIN
        IF :NEW.stan < 0 THEN
                raise_application_error(-20000, 'Stan zasobu ujemny');
        END IF;
END;
/

