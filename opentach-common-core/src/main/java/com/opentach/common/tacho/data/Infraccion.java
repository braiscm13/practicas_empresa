package com.opentach.common.tacho.data;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Infraccion {

  public String cif;
  public String nomEmp;
  public String numReq;
  public String idConductor;
  public String tipo;
  public java.util.Date fecIni, fecFin;
  public Number excon, fades;
  public String naturaleza;
  public String codBaremo;

  public Infraccion() {

  }

  public Infraccion( String numReq, String idConductor, String tipo, Date fecIni, Date fecFin ) {
    this.numReq = numReq;
    this.idConductor = idConductor;
    this.tipo = tipo;
    this.fecIni = fecIni;
    this.fecFin = fecFin;

  }
  public String toString() {
    SimpleDateFormat dfs = new SimpleDateFormat("(dd) HH:mm");
    String str="<html> </body><t>  <b>"+tipo+"</b>";
    if(cif!=null)
      str+="<br>Emp: "+cif+((nomEmp!=null)? " "+nomEmp : "" );
    if(idConductor!=null)
      str+="<br>Cond: "+idConductor;
    if(fecIni!=null)
      str+=  "<br><br>Ini:"+dfs.format(fecIni);
    if(fecFin!=null)
	str+="<br>Fin:"+dfs.format(fecFin);
    if(codBaremo!=null)
      str+=  "<br>Cod:"+codBaremo;
    str+="<br></body></html>";
    return str;
  }
}
