module namespace searchExtract = 'searchExtract';

import module namespace common 				= 'common' at '/APPLICATIONS/changeme/resources/xqueries/commonModules/common.xqy';
import module namespace utility 			= 'utility' at '/APPLICATIONS/changeme/resources/xqueries/commonModules/utility.xqy';
import module namespace dynamicQueryModule 	= 'dynamicQueryModule' at '/APPLICATIONS/changeme/resources/xqueries/commonModules/dynamicQueryModule.xqy';

declare variable $searchExtract:path := '/DATA/changeme/Collection/EXTRACT/';

declare function searchExtract:start($table,$Fdate,$Tdate,$restrictions,$first,$last,$currentuser){
	let $x 			:= searchExtract:computeDate($Fdate,$Tdate)
	let $fd 		:= replace(replace($x/@fromDate,"-",""),"/","")
	let $td 		:= replace(replace($x/@toDate,"-",""),"/","")
	let $table 		:= upper-case($table)
	let $main-query	:= searchExtract:searchload($table,$fd,$td)
	return common:getQuerySubsequence($main-query, $first, $last)
};

declare function searchExtract:computeDate($f,$t){
	let $f := xs:string($f)
	let $t := xs:string($t)
	return
		if(($f != "" and $t != "") or ($f = "" and $t = ""))
		then <x fromDate='{$f}' toDate='{$t}'/>
		else if($f != "" and $t = "")
			 then <x fromDate='{$f}' toDate=''/>
			 else <x fromDate='' toDate='{$t}' />
};

declare function searchExtract:searchload($table,$fd,$td){
	let $for 	:= concat(" for $i in doc('",$searchExtract:path,"')/EXTRACT/EXTRACT-ROW ")
	let $where 	:= dynamicQueryModule:queryWhereFramer("i", ($table,$fd,$td),("@TABLE","EXTRACTION_DATE","EXTRACTION_DATE"),("=",">=","<="))
	let $return := " return <result table='{$i/@TABLE}' date='{$i/EXTRACTION_DATE}' count='{$i/ROW_COUNT}' /> "
	return searchExtract:orderData(xhive:evaluate(concat($for,$where,$return)))
};

declare function searchExtract:orderData($results){
	for $i in distinct-values($results/@date)
	return
		for $j in distinct-values($results[@date = $i]/@table)
		return
			<result
				table	= '{$j}'
				date 	= '{searchExtract:dateConvert($i)}'
				count 	= '{((sum($results[@date = $i][@table = $j]/@count) cast as xs:integer) cast as xs:string)}'
				type    = 'dataExtractModule'
				title   = 'Extraction Details'
			/>
};

declare function searchExtract:dateConvert($input){
	if($input = "")
	then ""
	else concat(substring($input, 5, 2), "/", substring($input, 7, 2), "/", substring($input, 1, 4))
};