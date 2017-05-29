module namespace searchLoad = 'searchLoad';

import module namespace common 				= 'common' at '/APPLICATIONS/changeme/resources/xqueries/commonModules/common.xqy';
import module namespace utility 			= 'utility' at '/APPLICATIONS/changeme/resources/xqueries/commonModules/utility.xqy';
import module namespace dynamicQueryModule 	= 'dynamicQueryModule' at '/APPLICATIONS/changeme/resources/xqueries/commonModules/dynamicQueryModule.xqy';

declare variable $searchLoad:path := '/DATA/changeme/Collection/LOAD/';

declare function searchLoad:start($table,$Fdate,$Tdate,$restrictions,$first,$last,$currentuser){
	let $x 			:= searchLoad:computeDate($Fdate,$Tdate)
	let $fd 		:= replace(replace($x/@fromDate,"-",""),"/","")
	let $td 		:= replace(replace($x/@toDate,"-",""),"/","")
	let $table 		:= upper-case($table)
	let $main-query	:= searchLoad:searchload($table,$fd,$td)
	return common:getQuerySubsequence($main-query, $first, $last)
};

declare function searchLoad:computeDate($f,$t){
	let $f := xs:string($f)
	let $t := xs:string($t)
	return
		if(($f != "" and $t != "") or ($f = "" and $t = ""))
		then <x fromDate='{$f}' toDate='{$t}'/>
		else if($f != "" and $t = "")
			 then <x fromDate='{$f}' toDate=''/>
			 else <x fromDate='' toDate='{$t}' />
};

declare function searchLoad:searchload($table,$fd,$td){
	let $for 	:= concat(" for $i in doc('",$searchLoad:path,"')/LOAD/LOAD_ROW ")
	let $where 	:= dynamicQueryModule:queryWhereFramer("i", ($table,$fd,$td,"SUCCESS"),("@TABLE","DATE","DATE","@STATUS"),("=",">=","<=","="))
	let $return := " return <result table='{$i/@TABLE}' date='{$i/DATE}' count='{$i/ROW_COUNT}' /> "
	return searchLoad:orderData(xhive:evaluate(concat($for,$where,$return)))
};

declare function searchLoad:orderData($results){
	for $i in distinct-values($results/@date)
	return
		for $j in distinct-values($results[@date = $i]/@table)
		return
			<result
				table	= '{$j}'
				date 	= '{searchLoad:dateConvert($i)}'
				count 	= '{((sum($results[@date = $i][@table = $j]/@count) cast as xs:integer) cast as xs:string)}'
				type    = 'dataLoadModule'
				title   = 'Load Details'
			/>
};

declare function searchLoad:dateConvert($input){
	if($input = "")
	then ""
	else concat(substring($input, 5, 2), "/", substring($input, 7, 2), "/", substring($input, 1, 4))
};