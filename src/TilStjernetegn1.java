
public class TilStjernetegn1 {

    public static Integer findStjernetegn(int dag, int maaned) {
        if ((maaned == 3 && dag >= 21) || (maaned == 4 && dag <= 19)) return 0; // Vædder
        if ((maaned == 4 && dag >= 20) || (maaned == 5 && dag <= 20)) return 1; // Tyr
        if ((maaned == 5 && dag >= 21) || (maaned == 6 && dag <= 20)) return 2; // Tvilling
        if ((maaned == 6 && dag >= 21) || (maaned == 7 && dag <= 22)) return 3; // Krebs
        if ((maaned == 7 && dag >= 23) || (maaned == 8 && dag <= 22)) return 4; // Løve
        if ((maaned == 8 && dag >= 23) || (maaned == 9 && dag <= 22)) return 5; // Jomfru
        if ((maaned == 9 && dag >= 23) || (maaned == 10 && dag <= 22)) return 6; // Vægt
        if ((maaned == 10 && dag >= 23) || (maaned == 11 && dag <= 21)) return 7; // Skorpion
        if ((maaned == 11 && dag >= 22) || (maaned == 12 && dag <= 21)) return 8; // Skytte
        if ((maaned == 12 && dag >= 22) || (maaned == 1 && dag <= 19)) return 9; // Stenbuk
        if ((maaned == 1 && dag >= 20) || (maaned == 2 && dag <= 18)) return 10; // Vandmand
        if ((maaned == 2 && dag >= 19) || (maaned == 3 && dag <= 20)) return 11; // Fisk
        return -1; // Ukendt
    }
    int dag; int maaned;
    public void getStjernetegn(int dag, int maaned) {
        this.dag = dag;
        this.maaned = maaned;
    }

    public static Integer findStjernetegn(String fødselString){
        if(fødselString.length()<4)return -1;
        int dag=Integer.parseInt(fødselString.substring(0,2));
        int maaned=Integer.parseInt(fødselString.substring(2,4));
        return findStjernetegn(dag,maaned);

    }
}




/*
drop table if exists tbl1;

CREATE TABLE if not exists tbl1(
        navn name primary key,
        fødsel[]ArrayList,
        comment TEXT -- comment...
);

insert into tbl1 values ('David Berkowitz',010653,'tvilling');
insert into tbl1 values ('Albert Henry DeSalvo',030931,'jomfru');
insert into tbl1(navn,fødsel, comment) values ('Ted Bundy',241146,'skytten');
insert into tbl1 values ('Jeffrey Dhamer' , 21051960, 'tvilling');
insert into tbl1 values ('John Wayne Gacy' , 17031942, 'fisk');
insert into tbl1 values ('Ed Gein' ,27081906, 'jomfru');
insert into tbl1 values ('Richard Kuklinski' , 11041935, 'vædderen');
insert into tbl1 values ('Herman Webster Mudgett' , 16051861, 'tyren');
insert into tbl1 values ('Dennis Rader' , 09031945, 'fisk');
insert into tbl1 values ('Richard Ramirez' , 28021960, 'fisk');
insert into tbl1 values ('Gary Ridgway' , 180021949, 'vandmanden');
insert into tbl1 values ('Richard Speck' , 06121941 , 'skytten');
insert into tbl1 values ('Aileen Wuornos' , 29021956 , 'fisk');
insert into tbl1 values ('Joel Rifkin' , 20021959 , 'vandmanden');
insert into tbl1 values ('Robert Hansen' , 15021943 , 'vandmanden');
insert into tbl1 values ('Charles Jackson' , 120201937 , 'vandmanden');
insert into tbl1 values ('Derrick Todd Lee' , '05111968' , 'skorpion');
insert into tbl1 values ('Charles Manson' , 12111934 , 'skorpion');
insert into tbl1 values ('Edmund Kemper' , 18121948 , 'skytten');
insert into tbl1 values ('Rex Andrew Heuermann' , 130963, 'jomfru');



select * from tbl1;
*/
