module namespace recUnderPurge = 'recUnderPurge';

import module namespace common 				= 'common' at '/APPLICATIONS/changeme/resources/xqueries/commonModules/common.xqy';
import module namespace utility 			= 'utility' at '/APPLICATIONS/changeme/resources/xqueries/commonModules/utility.xqy';
import module namespace dynamicQueryModule 	= 'dynamicQueryModule' at '/APPLICATIONS/changeme/resources/xqueries/commonModules/dynamicQueryModule.xqy';

declare variable $recUnderPurge:limit := 1000;

declare function recUnderPurge:mainQuery($retention-policy-code,$record-type,$retain-until,$record-code,$restrictions,$first,$last,$currentuser){
	let $for 			:= " for $elem in doc('/DATA/changeme/Collection/Retention')/retention-records/retention-record "
	let $where 			:= dynamicQueryModule:queryWhereFramer("elem",($retention-policy-code,$record-type,$retain-until,$record-code),("retention-policy-code","record-type","retain-until","record-code"),("=","=","<=","="))
	let $purge_condtn 	:= "(xs:dateTime($elem/retain-until) lt current-dateTime())" 
	let $whereCorr 		:= if(contains($where,"where"))
						   then concat($where," and ", $purge_condtn)
						   else concat(" where ", $purge_condtn) 	
	let $return			:= " return <result
										retention-policy-id		= '{$elem/retention-policy-id}'
										retention-policy-code	= '{$elem/retention-policy-code}'
										retention-policy-period	= '{$elem/retention-policy-period}'
										record-id				= '{$elem/record-id}'
										record-code				= '{$elem/record-code}'
										record-name				= '{$elem/record-name}' 
										record-type				= '{$elem/record-type}' 
										base-date				= '{$elem/base-date}' 
										retain-until			= '{$elem/retain-until}' 
										on-hold					= '{$elem/on-hold}' 
										type					= 'retention_purge'
										title					= 'retention-record'
									/> "
	let $main-query := xhive:evaluate(concat("subsequence(",$for,$whereCorr,$return,",1,",$recUnderPurge:limit,")"))
    return common:getQuerySubsequenceLimitCheck($main-query, $first, $last, $recUnderPurge:limit)
};
