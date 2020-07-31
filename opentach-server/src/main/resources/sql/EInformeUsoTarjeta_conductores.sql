select distinct 
	idconductor 
from 
	cdactividades 
where
	fec_comienzo >= ? 
	and fec_fin <= ? 
	and numreq = ? 
	and matricula in (
						select distinct 
							matricula
						from 
							cdactividades a 
						where 
							fec_comienzo >= ? 
							and fec_fin <= ? 
							and a.numreq = ? 
							and (#CONDUCTORES#) 
					)
	and idconductor !='XXXXXXXXX'