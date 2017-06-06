module namespace retentionPolicies = 'retentionPolicies';

import module namespace common 				= 'common' at '/APPLICATIONS/changeme/resources/xqueries/commonModules/common.xqy';
import module namespace utility 			= 'utility' at '/APPLICATIONS/changeme/resources/xqueries/commonModules/utility.xqy';
import module namespace dynamicQueryModule 	= 'dynamicQueryModule' at '/APPLICATIONS/changeme/resources/xqueries/commonModules/dynamicQueryModule.xqy';

declare variable $retentionPolicies:limit := 1000;

declare function retentionPolicies:mainQuery($CategoryCode,$CountryName,$RegionName,$TotalRetentionPeriod,$restrictions,$first,$last,$currentuser){
	let $for 	:= " for $elem in doc('/DATA/changeme/Collection/Retention')/Retention/Retention-ROW "
	let $where 	:= dynamicQueryModule:queryWhereFramer("elem",($CategoryCode,$CountryName,$RegionName,$TotalRetentionPeriod),("CategoryCode","CountryName","Region","TotalRetentionPeriod"),("=","=","=","="))
	let $return	:= " return <result
								RetentionPolicyGUID	= '{$elem/GUID}' 
								Description			= '{$elem/Description}' 
								CategoryCode		= '{$elem/CategoryCode}' 
								CountryCode			= '{$elem/CountryCode}' 
								CountryName			= '{$elem/CountryName}' 
								RegionName			= '{$elem/RegionName}' 
								TotalRetentionPeriod= '{$elem/TotalRetentionPeriod}' 
								RetentionUnit		= '{$elem/RetentionUnit}' 
						        type				= 'retention_retentionpolicy'
								title				= 'Retention Policy'
							/> "
	let $main-query := xhive:evaluate(concat("subsequence(",$for,$where,$return,",1,",$retentionPolicies:limit,")"))
    return common:getQuerySubsequenceLimitCheck($main-query, $first, $last, $retentionPolicies:limit)
};



                                

