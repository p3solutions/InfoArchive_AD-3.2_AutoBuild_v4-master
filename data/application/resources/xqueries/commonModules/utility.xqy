module namespace utility = 'utility';

(: --------------------------------------------------------------------------------------------------------	:)
(:	DATE VARIABLE DECLARATION										   										:)
(: --------------------------------------------------------------------------------------------------------	:)
		declare variable $utility:epoch as xs:date := xs:date("1967-12-31");

		declare function utility:mask($input){
		 let $inputmask := string-to-codepoints($input)
		 return
			string-join(for $i at $counter in $inputmask
			return
				if($counter mod 2 = 1)
				then codepoints-to-string($i)
				else "#","")
			};


(: --------------------------------------------------------------------------------------------------------	:)
(:	Name 	: utility:spaceCheck												   						   		:)
(:	Usage	: validate presence of only space in the input provided										   	:)
(: --------------------------------------------------------------------------------------------------------	:)
declare function utility:spaceCheck($value)
{
	if(
		string-length(utility:trim($value)) = 0 and 
		string-length($value) > 0
	  ) 
	then "true" 
	else "false"
};

(: --------------------------------------------------------------------------------------------------------	:)
(:	Name 	: utility:spaceChecker									   						   					:)
(:	Usage	: pass set of inputs to the spaceCheck function													:)
(: --------------------------------------------------------------------------------------------------------	:)
declare function utility:spaceChecker($set)
{
	let $x := for $i in $set
			  where utility:spaceCheck($i) = "true"	
			  return 1
	return 
		if(count($x) = 0) 
		then "false"
		else "true" 
};
		
	
(: --------------------------------------------------------------------------------------------------------	:)
(:	Name 	: utility:mmddyyyy-to-date												   						   	:)
(:	Usage	: Converts a string in mm-dd-yyyy format to date format										   	:)
(: 	Input 	: String 																					   	:)
(:	Output 	: Date		   																				   	:)
(: --------------------------------------------------------------------------------------------------------	:)
		declare function utility:mmddyyyy-to-date($dateString as xs:string?)  as xs:date? 
		{
			if (empty($dateString)) then ()
			else if (not(matches($dateString,'^\D*(\d{2})\D*(\d{2})\D*(\d{4})\D*$'))) 
				 then error(xs:QName('utility:Invalid_Date_Format'))
				 else xs:date(replace($dateString,'^\D*(\d{2})\D*(\d{2})\D*(\d{4})\D*$','$3-$1-$2'))
		};
(: --------------------------------------------------------------------------------------------------------	:)
(: --------------------------------------------------------------------------------------------------------	:)




(: --------------------------------------------------------------------------------------------------------	:)
(:	Name 	: utility:genDateCincy													   						   	:)
(:	Usage	: Converts a epoch Date to mm-dd-yyyy format. Will handle an empty sequence				   	   	:)
(: 	Input 	: String 																					   	:)
(:	Output 	: String		   																			   	:)
(: --------------------------------------------------------------------------------------------------------	:)
		declare function utility:genDateCincy($DateInDays as xs:string?) as xs:string?
		{
			if($DateInDays cast as xs:string ='') 
			then '' 
			else
				let $epochDate 		:= xs:date("1967-12-31")
				let $DateAbs 		:= fn:abs($DateInDays cast as xs:integer)
				let $durationOp 	:= if ($DateInDays cast as xs:integer < 0) then "-P" else "P"
				let $DateDuration 	:= xs:dayTimeDuration(concat($durationOp ,concat($DateAbs,"D")))
				let $Date 			:= $epochDate + $DateDuration
				return 
					concat(substring(xs:string($Date),6,5),'-',substring(xs:string($Date),1,4)) 	
		};	
(: --------------------------------------------------------------------------------------------------------	:)
(: --------------------------------------------------------------------------------------------------------	:)




(: --------------------------------------------------------------------------------------------------------	:)
(:	Name 	: utility:replaceChar													   						   	:)
(:	Usage	: Replaces %20,%40,%60,etc with thier correspionding special character in a given string Date  	:)
(: 	Input 	: String 																					   	:)
(:	Output 	: String		   																			   	:)
(: --------------------------------------------------------------------------------------------------------	:)
		declare function utility:replaceChar($text as xs:string?) as xs:string?
		{
			let $text := replace($text,'%20',' ')
			let $text := replace($text,'%60','`')
			let $text := replace($text,'%40','@')
			let $text := replace($text,'%25','%')
			let $text := replace($text,'%2F','/')
			return 
				$text
		};
(: --------------------------------------------------------------------------------------------------------	:)
(: --------------------------------------------------------------------------------------------------------	:)




(: --------------------------------------------------------------------------------------------------------	:)
(:	Name 	: utility:dateFormatter													   						   	:)
(:	Usage	: Replaces ccyymmdd string to mm-dd-ccyy string												  	:)
(: 	Input 	: String 																					  	:)
(:	Output 	: String		   																			   	:)
(: --------------------------------------------------------------------------------------------------------	:)
		declare function utility:dateFormatter($date as xs:string?) as xs:string?
		{
			if($date != '' and $date != '00000000')
			then concat(substring($date,5,2),'-',substring($date,7,2),'-',substring($date,1,4))
			else ''
		};
(: --------------------------------------------------------------------------------------------------------	:)
(: --------------------------------------------------------------------------------------------------------	:)


(: date function to convert from '2013-03-09' to '03/09/2013'   format    :)

declare function utility:dateConvertFromYYYYMMDDToMMDDYYYY($input)
{
		let $req := if(normalize-space($input) != '') then
					(              
						let $d1 := if(contains($input, " ")) then substring-before($input, " ") else $input 
						let $d2 := concat(substring($d1, 6, 2), "/", substring($d1, 9, 2), "/", substring($d1, 1, 4))
						return $d2
					)
					else ()
	  let $result := if($req = ('12/31/9999','01/01/1753')) then ''
					else $req
					
		return $result
	
};


(: --------------------------------------------------------------------------------------------------------	:)
(:	Name 	: utility:Dollarformatter												   						   	:)
(:	Usage	: takes a double type value and passes it as string to FormatDollarV2 function				  	:)
(: 	Input 	: double 																					  	:)
(:	Output 	: String		   																			   	:)
(: --------------------------------------------------------------------------------------------------------	:)
		declare function utility:Dollarformatter($text as xs:double?)as xs:string?
		{
			utility:formatDollarV2((round-half-to-even(($text*100) cast as xs:double)) cast as xs:string)
		};
(: --------------------------------------------------------------------------------------------------------	:)
(: --------------------------------------------------------------------------------------------------------	:)


(: --------------------------------------------------------------------------------------------------------	:)
(:	Name 	: utility:Dollarformatter2												   						   	:)
(:	Usage	: takes a double type value and passes it as string to FormatDollarV2 function				  	:)
(: 	Input 	: double 																					  	:)
(:	Output 	: String		   																			   	:)
(: --------------------------------------------------------------------------------------------------------	:)
		declare function utility:Dollarformatter2($text as xs:double?)as xs:string?
		{
			utility:formatDollarV2(((round-half-to-even(($text*100) cast as xs:double)) cast as xs:integer) cast as xs:string)
		};
(: --------------------------------------------------------------------------------------------------------	:)
(: --------------------------------------------------------------------------------------------------------	:)


(: --------------------------------------------------------------------------------------------------------	:)
(:	Name 	: utility:Dollarformatter1												   						   	:)
(:	Usage	: takes a double type value and passes it as string to FormatDollarV2 function				  	:)
(: 	Input 	: double 																					  	:)
(:	Output 	: String		   																			   	:)
(: --------------------------------------------------------------------------------------------------------	:)
		declare function utility:Dollarformatter1($text as xs:double?)as xs:string?
		{
			utility:formatDollarV3((round-half-to-even(($text*1000) cast as xs:double)) cast as xs:string)
		};
(: --------------------------------------------------------------------------------------------------------	:)
(: --------------------------------------------------------------------------------------------------------	:)



(: --------------------------------------------------------------------------------------------------------	:)
(:	Name 	: utility:plus													   						   		   	:)
(:	Usage	: Replaces input string to 2 decimal formated String										   	:)
(: 	Input 	: String 																					   	:)
(:	Output 	: String		   																			   	:)
(: --------------------------------------------------------------------------------------------------------	:)
		declare function utility:plus($value as xs:string?) as xs:string?
		{
			if(starts-with($value,'+')) 
			then utility:formatDollarV2((((substring-after($value,'+')) cast as xs:integer) cast as xs:string)) 
			else if(starts-with($value,'-')) 
				 then concat('-',utility:formatDollarV2((((substring-after($value,'-')) cast as xs:integer) cast as xs:string)))
				 else $value
			
		};
(: --------------------------------------------------------------------------------------------------------	:)
(: --------------------------------------------------------------------------------------------------------	:)




(: --------------------------------------------------------------------------------------------------------	:)
(:	Name 	: utility:formatDollarV2													   						   	:)
(:	Usage	: Replaces input string to 2 decimal formated String										   	:)
(: 	Input 	: String 																					   	:)
(:	Output 	: String		   																			   	:)
(: --------------------------------------------------------------------------------------------------------	:)
(:
    formats the amount from an integer value of dollars and cents to be 
    in the format 123.99 and it handles some small drawbacks in utility:formatDollar()
:)
		declare function utility:formatDollarV2($amount as xs:string?) as xs:string?
		{
			let $length := string-length($amount)
		   return
				if (string(number($amount)) = 'NaN') then $amount
				else if($length = 0 or $amount eq '0') then '0.00' 
				else if( $length < 3 )then 
										if(substring($amount,1,1)='-') then concat('-0.0',substring($amount,2,1))
										else ( if($length=1) then concat('0.0',$amount)
										else concat('0.',$amount) )
				else if( ($length = 3) and (substring($amount,1,1) = '-') ) then
						 if(substring($amount,1,1)='-') then concat('-0.',substring($amount,2,2))
										else concat(substring($amount,1, $length - 2),'.',substring($amount,$length - 1))
				else 
					concat(substring($amount,1, $length - 2),'.',substring($amount,$length - 1))
			
		};
(: --------------------------------------------------------------------------------------------------------	:)
(: --------------------------------------------------------------------------------------------------------	:)


(: --------------------------------------------------------------------------------------------------------	:)
(:	Name 	: utility:formatDollarV3													   						   	:)
(:	Usage	: Replaces input string to 2 decimal formated String										   	:)
(: 	Input 	: String 																					   	:)
(:	Output 	: String		   																			   	:)
(: --------------------------------------------------------------------------------------------------------	:)
(:
    formats the amount from an integer value of dollars and cents to be 
    in the format 123.999 and it handles some small drawbacks in utility:formatDollar()
:)
		declare function utility:formatDollarV3($amount as xs:string?) as xs:string?
		{
			let $length := string-length($amount)
		   return
				if (string(number($amount)) = 'NaN') then $amount
				else if($length = 0 or $amount eq '0') then '0.000' 
				else if( $length < 4 )then 
										if(substring($amount,1,1)='-') then concat('-0.0',substring($amount,2,1))
										else ( if($length=1) then concat('0.00',$amount)
										else concat('0.',$amount) )
				else if( ($length = 4) and (substring($amount,1,1) = '-') ) then
						 if(substring($amount,1,1)='-') then concat('-0.',substring($amount,2,3))
										else concat(substring($amount,1, $length - 3),'.',substring($amount,$length - 2))
				else 
					concat(substring($amount,1, $length - 3),'.',substring($amount,$length - 2))
			
		};
(: --------------------------------------------------------------------------------------------------------	:)
(: --------------------------------------------------------------------------------------------------------	:)




(: --------------------------------------------------------------------------------------------------------	:)
(:	Name 	: utility:substring-before-if-contains									   						   	:)
(:	Usage	: returns substring value if string is available before the entered delimiter				   	:)
(: 	Input 	: String * (2)																				   	:)
(:	Output 	: String		   																			   	:)
(: --------------------------------------------------------------------------------------------------------	:)
declare function utility:substring-before-if-contains($arg as xs:string?,$delim as xs:string) as xs:string? 
{
	if (contains($arg,$delim))
	then substring-before($arg,$delim)
	else $arg
};
(: --------------------------------------------------------------------------------------------------------	:)
(: --------------------------------------------------------------------------------------------------------	:)




(: --------------------------------------------------------------------------------------------------------	:)
(:	Name 	: utility:substring-after-if-contains									   						   	:)
(:	Usage	: returns substring value if string is available after the entered delimiter				   	:)
(: 	Input 	: String * (2)																				   	:)
(:	Output 	: String		   																			   	:)
(: --------------------------------------------------------------------------------------------------------	:)
declare function utility:substring-after-if-contains($arg as xs:string?,$delim as xs:string) as xs:string? 
{
    if (contains($arg,$delim))
	then substring-after($arg,$delim)
	else $arg
};
(: --------------------------------------------------------------------------------------------------------	:)
(: --------------------------------------------------------------------------------------------------------	:)




(: --------------------------------------------------------------------------------------------------------	:)
(:	Name 	: utility:name-formatter													   						   	:)
(:	Usage	: formats the string removing leading and ending comma										   	:)
(: 	Input 	: String 																					   	:)
(:	Output 	: String		   																			   	:)
(: --------------------------------------------------------------------------------------------------------	:)
declare function utility:name-formatter($arg as xs:string?) as xs:string? 
{
	let $arg := if (ends-with($arg,', ')) 
				then utility:substring-before-if-contains($arg,', ') 
				else $arg
	let $arg := if (starts-with($arg,', ')) 
				then utility:substring-after-if-contains($arg,', ') 
				else $arg
	return $arg
};
(: --------------------------------------------------------------------------------------------------------	:)
(: --------------------------------------------------------------------------------------------------------	:)




(: --------------------------------------------------------------------------------------------------------	:)
(:	Name 	: utility:trim															   						   	:)
(:	Usage	: removes the leading and training spaces													   	:)
(: 	Input 	: String 																					   	:)
(:	Output 	: String		   																			   	:)
(: --------------------------------------------------------------------------------------------------------	:)
declare function utility:trim ( $arg as xs:string? )  as xs:string 
{
	replace(replace($arg,'\s+$',''),'^\s+','')
} ;
(: --------------------------------------------------------------------------------------------------------	:)
(: --------------------------------------------------------------------------------------------------------	:)




(: --------------------------------------------------------------------------------------------------------	:)
(:	Name 	: utility:wildCardName												   						   		:)
(:	Usage	: formats the wild card text entered 														   	:)
(: 	Input 	: String 																					   	:)
(:	Output 	: String		   																			   	:)
(: --------------------------------------------------------------------------------------------------------	:)
declare function utility:wildCardName($text)
{
	replace($text,' ',".*&amp;amp;.*")
};
(: --------------------------------------------------------------------------------------------------------	:)
(: --------------------------------------------------------------------------------------------------------	:)



(: OTHER FUNCTIONS FROM MAHP MODULES :)
declare function utility:isRestricted($restrictions as xs:string, $targetField as xs:string) 
as xs:string? 
{
    let $tokens := tokenize($restrictions, '[ ,]+')
    return if(exists(index-of($tokens, $targetField)))
		   then 'Y'
		   else 'N'
};

declare function utility:filterConfidential($records as element()*, $restrictions as xs:string)
as element()*
{
    ()
};

(: does not handle empty sequences :)
declare function utility:genDate($dateInDays as xs:integer) 
as xs:date
{
    let $epochDate := xs:date($utility:epoch)
    let $dateAbs := fn:abs($dateInDays)
    let $durationOp := if ($dateInDays < 0) then "-P" else "P"
    let $dateDuration := xs:dayTimeDuration(concat($durationOp ,concat($dateAbs,"D")))
    let $calculatedDate := $epochDate + $dateDuration 
    return 
        $calculatedDate
};


(: 
    Will pass-through an empty sequence, otherwise a date item,
    which if cast to a string is lexigraphically sortable.
:)
declare function utility:genDate2($dateInDays as item()?) 
as xs:date?
{
    if (utility:has($dateInDays)) then
        let $epochDate := xs:date("1967-12-31")
        let $dateAbs := fn:abs($dateInDays)
        let $durationOp := if ($dateInDays < 0) then "-P" else "P"
        let $dateDuration := xs:dayTimeDuration(concat($durationOp ,concat($dateAbs,"D")))
        let $calculatedDate := $epochDate + $dateDuration 
        return 
            $calculatedDate
    else 
        ()
};



(: DisplayFromEpochString :)

declare function utility:MM-DD-YYYY-DisplayFromEpochString($dateInDays as xs:string) 
as xs:string
{  
     if ($dateInDays = '') then
        ''
     else                    
        let $dDate :=  utility:genDate2(xs:integer($dateInDays))
        return 
           utility:dateAsMM-DD-YYYY ($dDate)
};


(: DisplayFromEpoch :)

declare function utility:MM-DD-YYYY-DisplayFromEpoch($dateInDays as item()?) 
as xs:string
{  
     if (utility:has($dateInDays)) then
        let $dDate :=  utility:genDate2($dateInDays)
        return 
           utility:dateAsMM-DD-YYYY ($dDate)
     else 
        ""
};


declare function utility:userDate($input as xs:string)
as xs:date?
{
    if (utility:areAllDigits($input)) then
        utility:genDate(xs:integer($input))
    else (: continue on :)
    let $tokens := tokenize($input, '[^0-9]+')
    return if (count($tokens) != 3) then () else (: continue on :)
    
    let $year := 
        for $token in $tokens
        where string-length($token) = 4
        return $token
    
    return if(count($year) != 1) then () else (: continue on :)
    
    let $remTokens := 
        for $token in $tokens
        where string-length($token) le 2
        return $token
    
    return if(count($remTokens) != 2) then () else (: continue on :)
    
    let $month := $remTokens[1]
    let $month := if (string-length($month) = 1) then concat('0', $month) else $month
    let $day := $remTokens[2]
    let $day := if (string-length($day) = 1) then concat('0', $day) else $day
    
    return xs:date(concat($year, '-', $month, '-', $day))
};

declare function utility:MM-DD-YYYYasDate($value as xs:string) 
as xs:date 
{
    xs:date(concat(substring($value, 7), '-', substring($value, 1, 5))) 
};

declare function utility:YYYY-MM-DDasMM-DD-YYYY($date as xs:string)
as xs:string
{
    concat(substring($date, 6, 5), '-', substring($date, 1, 4))
};

declare function utility:dateAsMM-DD-YYYY($date as xs:date?)
as xs:string
{
    if (empty($date)) then
        ''
    else
        let $standard := string($date)
        return concat(substring($standard, 6, 5), '-', substring($standard, 1, 4))
};

declare function utility:MM-DD-YYYYasEpochDays($dateStr as xs:string) 
as xs:integer 
{
    let $date as xs:date := utility:MM-DD-YYYYasDate($dateStr)
    return 
        utility:dateAsEpochDays($date)
};

declare function utility:dateAsEpochDays($date as xs:date) 
as xs:integer 
{
    days-from-duration($date - $utility:epoch)
};

declare function utility:stdEpochDays($date as xs:string) 
as xs:integer 
{
    utility:dateAsEpochDays(xs:date($date))
};

(: DEPRECATED - Use utility:hasContent(element()*) instead :)
(: Assumes sequences of non-empty items.  Having an empty item in the sequence does not cause false to be returned. :)
declare function utility:allHaveLength($args as item()*) 
as xs:boolean 
{
    let $good :=
        for $arg in $args
        where string-length($arg) > 0
        return 
            $arg
    return 
        count($good) = count($args)
};

declare function utility:hasContent($elements as element()*)
as xs:boolean
{
    some $elem in $elements 
    satisfies
        some $text in $elem/descendant-or-self::*/text()
        satisfies string-length(string($text)) gt 0
};

(: 
has($arg) will return false if $arg is null or an empty string.  
Error if arg cannot be converted to a string.
:)
declare function utility:has($arg as item()?) 
as xs:boolean 
{
    if (empty($arg)) then
        false()
    else
        string-length(string($arg)) gt 0
};

(: 
isNull($arg) will return true if $arg is null or an empty string.  
Error if arg cannot be converted to a string.
:)
declare function utility:isNull($arg as item()?) 
as xs:boolean 
{
    if (empty($arg)) then
        true()
    else
        string-length(string($arg)) = 0
};

declare function utility:getMetadataPath($record as element())
as xs:string
{
    let $xml-uri := base-uri($record)
    let $metadata-uri := replace($xml-uri, '/Collection/', '/CollectionMetadata/')
    return replace($metadata-uri, '/[^/]*$', '/')
};

(:
    Generates a sequence of XML element names based on metadata column lookup.
    Guarantees the input column sequence and the output name sequence are the same
    length and each index correctly maps between them.
:)
declare function utility:getMetadataNames($record as element(), $tableName as xs:string,
    $colNumbers as xs:integer*) 
as xs:string*
{
    let $path := utility:getMetadataPath($record)
    let $root := doc($path)/METADATA[TABLEMETADATA/@uctablename = $tableName]
    let $childNames := 
        for $colno in $colNumbers
        let $name := $root/DATATYPES-MV[COLNO = $colno]/NAME
        return 
            if (empty($name)) then 
                error((), concat('Failed metadata lookup. column: ', string($colno), 
                    ' subaccount: ', $tableName, ' path: ', $path))
            else string($name)
    return
        if (empty($root)) then 
            error((), concat('Unable to find metadata table named: ', $tableName))
        else
            $childNames
};

declare function utility:identifyMvsByChildren($record as element()?, $names as xs:string*)
{
    let $kidCount := count($names)
    for $elem in $record//*
    let $presentKids :=
        for $name in $names
        where exists($elem/child::*[local-name() = $name])
        return $name
    where count($presentKids) = $kidCount
    return 
        $elem
};

(:
    This function returns the multi-value considered effective for a given date
    of service.  An effective multi-value will have the youngest date without 
    going over the date of service.  In other words, the most current and applicable
    multi-value at a given time.

    Returns the youngest multivalue at the time of the date of service (DOS).
    For example, $DOS = 5, and $multivalues/$dateName is (10,8,7,4,3,2).  The sorting is
    performed by this function.  The multivalue with date 4 would be returned.
:)
declare function utility:getEffectiveMV($multivalues as item()*, $DOS as xs:integer, 
$dateName as xs:string) 
as element()?
{
    let $nonEmptyMultivalues :=
        for $mv in $multivalues
        where utility:has($mv/child::*[local-name() = $dateName])
        return $mv
    let $priorMultivalues := 
        for $mv in $nonEmptyMultivalues
        let $date as xs:integer := xs:integer($mv/child::*[local-name() = $dateName])
        where $DOS ge $date
        order by $date descending
        return $mv
    return 
        $priorMultivalues[1]
};

declare function utility:getEffectiveMV($record as element()?, $mvName as xs:string, 
    $DOS as xs:integer, $dateName as xs:string) 
as element()?
{
    let $mvs := $record//*[local-name() = $mvName]
    return 
        utility:getEffectiveMV($mvs, $DOS, $dateName)
};

declare function utility:getDynamicEffectiveMV($record as element()?, $DOS as xs:integer, 
    $otherCols as xs:integer*, $dateCol as xs:integer)
as element()?
{
    if (empty($record)) then () else 
       let $tableName as xs:string := string($record/parent::*/@filename)
       let $allCols as xs:integer* := ($dateCol, $otherCols)
       let $childNames as xs:string* := utility:getMetadataNames($record, $tableName, $allCols)
       let $mvs := utility:identifyMvsByChildren($record, $childNames)
       return 
           (: the first name *should* be the date name since $allCols was constructed that way :)
           utility:getEffectiveMV($mvs, $DOS, $childNames[1])
};

(:
    A convenience function that returns a value in a manner to this example pseduo-code:
        
    Compare the dates in attribute 93 against the DOS and find the appropriate match.
    PPO.ONLY = R.CODE<92,DT.POS>
    If PPO.ONLY is null then PPO.ONLY = R.CODE<34>
    
    This implementation is not sensitive to the multi-values being direct children
        of $record, so multi-value wrappers will not break the code.
    
    The parameter $defaultName is the name of a direct child of $record.
:)
declare function utility:getEffectiveValue($record, $mvName as xs:string, $DOS as xs:integer, 
$targetName as xs:string, $dateName as xs:string, $defaultName as xs:string)
{
    let $effectiveMV := utility:getEffectiveMV($record, $mvName, $DOS, $dateName)
    let $target := $effectiveMV/child::*[local-name() = $targetName]
    let $default := $record/child::*[local-name() = $defaultName]
    return 
        if(utility:has($target)) then 
            $target 
        else
            $default
        (:else if (utility:has($default)) then
            $default
        else
            error((), concat('Failed to find effective value, target name: ', $targetName, 
                ' default name: ', $defaultName)):)
};

declare function utility:getEffectiveValue($multivalues as item()*, $DOS as xs:integer, 
$targetName as xs:string, $dateName as xs:string)
{
    let $effectiveMV := utility:getEffectiveMV($multivalues, $DOS, $dateName)
    return 
        $effectiveMV/child::*[local-name() = $targetName]
};

declare function utility:getDynamicEffectiveValue($record as element()?, $DOS as xs:integer, 
    $targetCol as xs:integer, $dateCol as xs:integer, $defaultCol as xs:integer)
{
    (:if (empty($record)) then 
        error((), 'Null record in getDynamicEffectiveValue(5)')
    else :)
    if (empty($record)) then () else 
        let $tableName as xs:string := string($record/parent::*/@filename)
        let $allCols as xs:integer* := ($dateCol, $targetCol, $defaultCol)
        let $childNames as xs:string* := utility:getMetadataNames($record, $tableName, $allCols)
        let $dateName := childNames[1]
        let $targetName := childNames[2]
        let $defaultName := childNames[3]
        let $mvs := utility:identifyMvsByChildren($record, $childNames)
        (: the first name *should* be the date name since $allCols was constructed that way :)
        let $effectiveMV := utility:getEffectiveMV($mvs, $DOS, $dateName)
        let $target := $effectiveMV/child::*[local-name() = $targetName]
        let $default := $record/child::*[local-name() = $defaultName]
        return 
            if(utility:has($target)) then 
                $target 
            (:else
                $default:)
            else if (utility:has($default)) then
                $default
            else
                error((), concat('Failed to find effective value, target name: ', $targetName, 
                    ' default name: ', $defaultName))
};

declare function utility:getDynamicEffectiveValue($record as element()?, $DOS as xs:integer, 
    $targetCol as xs:integer, $dateCol as xs:integer)
{
    (:if (empty($record)) then 
        error((), 'Null record in getDynamicEffectiveValue(4)')
    else :)
    if (empty($record)) then () else 
       let $tableName as xs:string := string($record/parent::*/@filename)
       let $allCols as xs:integer* := ($dateCol, $targetCol)
       let $childNames as xs:string* := utility:getMetadataNames($record, $tableName, $allCols)
       let $mvs := utility:identifyMvsByChildren($record, $childNames)
       (: the first name *should* be the date name since $allCols was constructed that way :)
       let $effectiveMV := utility:getEffectiveMV($mvs, $DOS, $childNames[1])
       let $target := $effectiveMV/child::*[local-name() = $childNames[2]]
       return $target
};

(:
    A multi-value centric function that finds the MV with date equal to DOS
    and returns the associated value.  Possibly multiple values will be returned.
:)
declare function utility:getDatedValue($record, $DOS as xs:integer, 
$targetName as xs:string, $dateName as xs:string)
{
    let $parent := $record//*[local-name() = $dateName and . = $DOS]/parent::*
    return 
        $parent/child::*[local-name() = $targetName]
};

(:
    formats the amount from an integer value of dollars and cents to be 
    in the format 123.99
:)
declare function utility:formatDollar($amount)
{
    let $length := string-length($amount)
    let $formatted := 
        if($length = 0 or $amount eq '0') then 
            '0.00' 
        else 
            concat(substring($amount,1, $length - 2),'.',substring($amount,$length - 1))
    return 
        $formatted
};



(:
    formats the amount from an integer value of dollars and cents to be 
    in the format 123.99 and it add additional functionallity to utility:formatDollarV2() 
	which display amount as 'n,nnn,nnn.nn' format
:)

declare function utility:formatDollarV4($amount)
{
	let $amount := string($amount)
    let $length := string-length($amount)
    let $part1		:= if (string(number($amount)) = 'NaN') 
						then <res type='1' value='{$amount}'/>
				   else if($length = 0 or $amount eq '0') 
							then <res type='2' value='0.00'/>
	  			   else if ($length < 3) 
							then 
								if(substring($amount,1,1)='-') 
									then <res type='3' value='{concat('-0.0',substring($amount,2,1))}' />
								else ( 
										if($length=1) 
											then <res type='3' value='{concat('0.0',$amount)}' />
											else <res type='3' value='{concat('0.',$amount)}' /> 
									 )
				   else if (($length = 3) and (substring($amount,1,1) = '-')) 
							then
								if(substring($amount,1,1)='-') 
									then <res type='4' value='{concat('-0.',substring($amount,2,2))}'/>
								else <res type='4' value='{concat(substring($amount,1, $length - 2),'.',substring($amount,$length - 1))}' />
				   else <res type='5' value='{concat(substring($amount,1, $length - 2),'.',substring($amount,$length - 1))}'/>
	
	let $final := if($part1/@type = '5' and string-length($amount) >=5)
					then 
						let $z:= for $i in 0 to (string-length($part1/@value)-3)
									let $a:= substring($part1/@value,((string-length($part1/@value)-3)-$i),1)
									let $b := if(($i cast as xs:integer -2) mod 3 = 0 and $i != (string-length($part1/@value)-3)-1 and substring($part1/@value,(((string-length($part1/@value)-3)-$i)-1),1) !='-') 
												then concat(',',$a) 
											  else $a
									return $b
						let $zz := for $i in 1 to count($z)-1 return $z[count($z)-$i]
						let $zzz := concat(string-join($zz,''),substring($part1/@value,(string-length($part1/@value)-2),3))
						return $zzz
					else data($part1/@value)	
	return $final
};
(:
    formats the amount from an decimal/float value of dollars and cents to be 
    in the format 123.99 and also display amount as 'n,nnn,nnn.nn' format
:)
declare function utility:roundDollar($round)
{
let $a := round-half-to-even((number($round)) * 100) cast as xs:decimal
let $b := $a cast as xs:string
let $c := string-length($b)
let $d := concat(substring($b,1,$c - 2),'.',substring($b,$c - 1))
let $amount2 := (xs:decimal($d))*100
let $amount := xs:string($amount2)
let $length := string-length($amount)
    let $part1		:= if (string(number($amount)) = 'NaN') 
						then <res type='1' value='{$amount}'/>
				   else if($length = 0 or $amount eq '0') 
							then <res type='2' value='0.00'/>
	  			   else if ($length < 3) 
							then 
								if(substring($amount,1,1)='-') 
									then <res type='3' value='{concat('-0.0',substring($amount,2,1))}' />
								else ( 
										if($length=1) 
											then <res type='3' value='{concat('0.0',$amount)}' />
											else <res type='3' value='{concat('0.',$amount)}' /> 
									 )
				   else if (($length = 3) and (substring($amount,1,1) = '-')) 
							then
								if(substring($amount,1,1)='-') 
									then <res type='4' value='{concat('-0.',substring($amount,2,2))}'/>
								else <res type='4' value='{concat(substring($amount,1, $length - 2),'.',substring($amount,$length - 1))}' />
				   else <res type='5' value='{concat(substring($amount,1, $length - 2),'.',substring($amount,$length - 1))}'/>
	
	let $final := if($part1/@type = '5' and string-length($amount) >=5)
					then 
						let $z:= for $i in 0 to (string-length($part1/@value)-3)
									let $a:= substring($part1/@value,((string-length($part1/@value)-3)-$i),1)
									let $b := if(($i cast as xs:integer -2) mod 3 = 0 and $i != (string-length($part1/@value)-3)-1 and substring($part1/@value,(((string-length($part1/@value)-3)-$i)-1),1) !='-') 
												then concat(',',$a) 
											  else $a
									return $b
						let $zz := for $i in 1 to count($z)-1 return $z[count($z)-$i]
						let $zzz := concat(string-join($zz,''),substring($part1/@value,(string-length($part1/@value)-2),3))
						return $zzz
					else data($part1/@value)	
	return $final
	};

(:
    Formats the integer value, in cents, to dollars.  
    Non-numeric values pass through as-is, and null values become ''.
:)
declare function utility:formatDollar2($amount)
as xs:string
{
    let $amount := string($amount)
    let $length := string-length($amount)
    return 
        if (string(number($amount)) = 'NaN') then
            $amount
        else if ($amount eq '0') then 
            '0.00' 
        else 
            concat(substring($amount,1, $length - 2),'.',substring($amount,$length - 1))
};

declare function utility:areAllDigits($value as item()?) 
as xs:boolean 
{
    let $reduced := translate(string($value), '0123456789', '')
    return 
        string-length($reduced) = 0
};

declare function utility:areAllAlphabetic($value as item()) 
as xs:boolean 
{
    let $reduced := translate(string($value), 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ', '')
    return 
        string-length($reduced) = 0
};

(:
    The functx:replace-first function replaces, within $arg, the first area that matches $pattern) with $replacement.
    If no area matches the pattern, no replacement is made.
:)
declare function utility:replace-first($arg as xs:string? , $pattern as xs:string , $replacement as xs:string)
as xs:string
{
    fn:replace($arg, fn:concat('(^.*?)', $pattern),
             fn:concat('$1',$replacement))
};

(:
    calcluateAge from a epoc date
:)
declare function utility:calcluateAge($birthDay as xs:integer)
as xs:integer
{
    let $today := current-date()
    let $bdate := utility:genDate2($birthDay)
    let $age :=
        (($today  - $bdate ) div xs:dayTimeDuration('P1D')) 
                idiv 365.242199  * xs:yearMonthDuration('P1Y')
      
    return years-from-duration($age)
    
};

(:
    calcluateAge from a epoc date
:)
declare function utility:calcluateAge($birthDay as xs:integer, $calculateDate as xs:integer)
as xs:integer
{
    let $today := utility:genDate2($calculateDate)
    let $bdate := utility:genDate2($birthDay)
    let $age :=
        (($today  - $bdate ) div xs:dayTimeDuration('P1D')) 
                idiv 365.242199  * xs:yearMonthDuration('P1Y')
      
    return years-from-duration($age)
    
};




(: Convert the Epoch days into MM-DD-YYYY date format :)

declare function utility:genDateMM-DD-YYYY($dateInDays as item()*) 
as item()*
{
    if (utility:has($dateInDays)) then
        let $epochDate := xs:date("1967-12-31")
        let $dateAbs := fn:abs(xs:integer($dateInDays))
        let $durationOp := if ($dateInDays < 0) then "-P" else "P"
        let $dateDuration := xs:dayTimeDuration(concat($durationOp ,concat($dateAbs,"D")))
        let $calculatedDate := $epochDate + $dateDuration
        let $formattedDate := concat(substring(xs:string($calculatedDate),6,5),'-',substring(xs:string($calculatedDate),1,4))  
        return 
            $formattedDate
    else 
        ()
};


(: Convert the Epoch days into MM-DD-YYYY date format and also handles non-numeric data :)

declare function utility:genDateMM-DD-YYYYV2($dateInDays as item()*) 
as item()*
{
    if (utility:has($dateInDays)) then
        if(string(number($dateInDays)) = 'NaN') then $dateInDays
        else
	(
        let $epochDate := xs:date("1967-12-31")
        let $dateAbs := fn:abs(xs:integer($dateInDays))
        let $durationOp := if ($dateInDays < 0) then "-P" else "P"
        let $dateDuration := xs:dayTimeDuration(concat($durationOp ,concat($dateAbs,"D")))
        let $calculatedDate := $epochDate + $dateDuration
        let $formattedDate := concat(substring(xs:string($calculatedDate),6,5),'-',substring(xs:string($calculatedDate),1,4))  
        return 
            $formattedDate
	)	
    else 
        ()
};

declare function utility:string($vals as item()*)
as xs:string?
{
    let $strings := 
        for $val in $vals
        return string($val)
    return string-join($strings, ', ')
};

(: Convert the Epoch time into HH:MM:SS time format :)

declare function utility:genTime($timeInSeconds as item()*) 
as item()*
{
	if (utility:has($timeInSeconds)) then
		if ($timeInSeconds != 0) then
        	let $hh := $timeInSeconds idiv 3600
    		let $hh1 := if ($hh != 0) then
    						if (number($hh) <= 9) then
        						let $hh1 := concat(0, $hh)
        						return $hh1
      						else
        						$hh
    					else
      						let $hh1 := "00"
      						return $hh1
    		let $omm := $timeInSeconds - ($hh * 3600)
    		let $mm := $omm idiv 60
    		let $mm1 := if ($mm != 0) then
    						if (number($mm) <= 9) then
        						let $mm1 := concat(0,$mm)
        						return $mm1	
      						else
        						$mm
    					else
      						let $mm1 := "00"
      						return $mm1
    		let $ss := $omm - ($mm * 60)
    		let $ss1 := if ($ss != 0) then
    						if (number($ss) <= 9) then
        						let $ss1 := concat(0, $ss)
        						return $ss1
      						else
        						$ss
    					else
	      					let $ss1 := "00" 
      						return $ss1				
			let $formattedtime := concat($hh1, ':', $mm1, ':', $ss1)
			return $formattedtime
  		else ()
	else if (utility:has($timeInSeconds)) then
		if  ($timeInSeconds = 0) then
  			let $formattedtime := xs:string("00:00:00")
  			return $formattedtime
    	else ()	
	else
    ()
};


(: The below function utility:genTimeAMPM can be used to display time in AM/PM format 
   Eg : 54321 will be converted to '03:05:21 PM' 
   Usage : The output of utility:genTime should be given as input to utility:genTimeAMPM
   Sample : utility:genTimeAMPM(utility:genTime(54321)) will give '03:05:21 PM' as output :)
   
declare function utility:genTimeAMPM($Time)
{
	if (utility:has($Time))	then
           let $hour :=  if(substring(xs:string($Time),2,1) = ':') then substring(xs:string($Time),1,1) else substring(xs:string($Time),1,2)
           let $hourx := $hour cast as xs:integer
           let $returnhour1 := if($hourx >12) then ($hourx - '12' cast as xs:integer) else $hourx
           let $returnhour := if($returnhour1< 10) then concat('0',$returnhour1) else $returnhour1
           let $timeformat := if($hourx >=12) then 'PM' else 'AM'
              return if(substring(xs:string($Time),2,1) = ':') then concat($returnhour,substring(xs:string($Time),2),' ',$timeformat) 
              	   else concat($returnhour,substring(xs:string($Time),3),' ',$timeformat)
	else ()
};

 
 declare function utility:buildPredicate($basePredicate as xs:string, $childPath as xs:string, $operator as xs:string, $targetValue as xs:string?, $quoteValue as xs:boolean)
 as xs:string
 {
    if (utility:isNull($targetValue)) then
        $basePredicate
    else
        let $targetValue := if ($quoteValue) then concat('"', $targetValue, '"') else $targetValue
        return
            if (utility:has($basePredicate)) then
                string-join(($basePredicate, 'and', $childPath, $operator, $targetValue), ' ')
            else
                string-join(($childPath, $operator, $targetValue), ' ')
 };
 
 

(:
    The getStringIntegerFromItem function. 
:)
declare function utility:getStringIntegerFromItem($s as  item()?) as xs:string
{ 
     let $str := string($s)
     return
       if (empty($s)) then 
          '0'
       else if ($str = "") then      
          '0'
       else 
          $str
};

(: Formats telephone number if I/P is '1234567890' O/P will be '(123)456-7890'. If the I/P is formatted it is returned as it is :)
declare function utility:formatPhone($phone as item()*) 
as item()*
{ 
let $out := if (utility:has($phone)) then 
		 		if (utility:areAllDigits($phone)) then	
					let $phone1 := substring($phone,1,3)
					let $phone2 := substring($phone,4,3)
					let $phone3 := substring($phone,7,4)
						return concat('(',$phone1,')',' ',$phone2,' - ',$phone3)
				else $phone
			else ()
return $out
};

(:
    In XQuery, the standard predefined XML entities must be used when writing code.
    When dynamically building a string for evaluation, all XML content (text and attributes)
    must be escaped.  For example, if inserting data into a dynamic query, the data may
    contain an ampersand, less-than, etc., and if simply concatenated into a query string
    it is open for parse failure or arbitrary injection.
:)
declare function utility:escapeForXML($content as xs:string?)
as xs:string?
{
    if (empty($content)) then
        ()
    else
        let $c := replace($content, '&amp;', '&amp;amp;')
        let $c := replace($c, '&quot;', '&amp;quot;')
        let $c := replace($c, '&apos;', '&amp;apos;')
        let $c := replace($c, '&gt;', '&amp;gt;')
        let $c := replace($c, '&lt;', '&amp;lt;')
        return $c
};

declare function utility:is-a-number( $value as xs:anyAtomicType? )
as xs:boolean 
{
    string(number($value)) != 'NaN'
} ;

(: utility:isConfidential is mainly used for checking the Confidential rows which are placed in seperate xml file and returns true or false as output.
  But this function is versatile and can be used to check for a particular id to be present in XML file.
  Usage : This function takes 4 inputs 1. Area Name 2. File Name 3. Id Tag name 4. Id
  Eg : To check the existence of XML_ID='V5095' in CODE file, one can use this function as follows
  		utility:isConfidential($area,$file,$idTag,$xmlId) where $area is 'CLAIMS', $file is 'CODE', $idTag is 'XML_ID' and $xmlId is 'V5095' :)
  		
declare function utility:isConfidential($area as item()?,$file as item()?,$idTag as item()?,$xmlId as item()?) 
as xs:boolean 
{
let $data := concat("doc('/DATA/demo/Collection/",$area,"/",$file,"/')/",$file,"/",$file,"-ROW[",$idTag," ='",$xmlId,"']/",$idTag)
return if (utility:has($area) and utility:has($file) and utility:has($idTag) and utility:has($xmlId))  
	then
		string-length(xhive:evaluate($data)) > 0 
	else 
		false() 
};



(: -------------------------------------------GLHP Common Functions-------------------------------------------------------------	  :)
declare function utility:removeNull($nodes as node()* )  as node()*
{
	let $t1 := for $i in $nodes
	let $t2 := for $j in ($i/@*) return count(string($j)[.!=''])
	return <res tmp='{max($t2)}'/>
	let $c := count($nodes)

	let $res :=
		for $x in (1 to $c)
		return
		(
		if($t1[$x]/@tmp = '0') then ()
		else 
		let $tmp := $nodes[$x]
		return $tmp
		)
	return $res
};

declare function utility:distinct-deep( $nodes as node()* )  as node()* 
{
	for $seq in (1 to count($nodes))
	return $nodes[$seq][not(utility:is-node-in-sequence-deep-equal(.,$nodes[position() < $seq]))]
 } ;
		 
declare function utility:is-node-in-sequence-deep-equal( $node as node()? ,$seq as node()* )  as xs:boolean 
{
   some $nodeInSeq in $seq satisfies deep-equal($nodeInSeq,$node)
} ;

declare function utility:is-node-in-sequence( $node as node()? ,	$seq as node()* )  as xs:boolean 
{
   some $nodeInSeq in $seq satisfies $nodeInSeq is $node
} ;

declare function utility:distinct-nodes ( $nodes as node()* )  as node()* 
{
	for $seq in (1 to count($nodes))
	return $nodes[$seq][not(utility:is-node-in-sequence(.,$nodes[position() < $seq]))]
};

declare function utility:dateFormat($input)
{
let $req :=
	if(normalize-space($input) != '') 
	then
		(	
		let $d1 := if(contains($input, " ")) then substring-before($input, " ") else $input	
		let $d2 := concat(substring($d1, 6, 2), "/", substring($d1, 9, 2), "/", substring($d1, 1, 4))
		return $d2
		)
	else ()
return $req
};

declare function utility:MM-DD-YYYYasDate1($value as xs:string) 
as xs:string 
{
    xs:string(concat(substring($value, 7,4),'-',substring($value, 1, 2),'-', substring($value, 4, 2))) 
};

declare function utility:formatDollarIndemo($var as xs:string?)
{
let $a := $var cast as xs:string
let $len := string-length($a) cast as xs:integer -2
let $dollar := substring ($a,1,$len)
let $amount := if (starts-with($dollar,'-')) then concat('{','$',replace($dollar,'-',''),'}') else concat('$',$dollar)
return $amount
};

(: below function will remove a node when all the attributes present in that node are null/blank :)
declare function utility:removeAttrIfAllNull($nodes as node()* )  as node()*
{
let $t1 := for $i in $nodes
let $t2 := for $j in ($i/@*) return count(string($j)[.!=''])
return <res tmp='{max($t2)}'/>
let $c := count($nodes)

let $res :=
for $x in (1 to $c)
return
(
if($t1[$x]/@tmp = '0') then ()
else 
let $tmp := $nodes[$x]
return $tmp
)
return $res
};

(: Displays current date in MM-DD-YYYY format :)
declare function utility:currentDateMM-DD-YYYY() 
{
      let $date 	:= substring(string(current-date()),1,10)
      let $format	:= if ($date != '') then
						  let $date 	:= string($date)
						  let $year 	:= substring($date,1,4)
						  let $month	:= substring-before(substring-after($date,'-'),'-')
						  let $date 	:= substring($date,9,10)
						  let $reqDate 	:= concat($month,'-',$date,'-',$year)
						  let $corrDate := $reqDate 
						  return $corrDate 
					  else()
     return $format
};


declare function utility:date_chk($date)
{
	let $date := if($date = '01/01/1753' or $date = '12/31/9999' or $date = '01/01/1920' or $date = '12/31/2199') 
				then ''
				else $date
	return $date

};

declare function utility:formatZip($zip as item()*) 
as item()*
{ 
let $out := if (utility:has($zip)) then
					(
		 		if (utility:areAllDigits($zip) and (string-length($zip)=9)) then	
					let $zip1 := substring($zip,1,5)
					let $zip2 := substring($zip,6,4)
						return concat($zip1,'-',$zip2)
				else if (utility:areAllDigits($zip) and (string-length($zip)=11)) then
					let $zip1 := substring($zip,1,5)
					let $zip2 := substring($zip,6,4)
					let $zip3 := substring($zip,10,2)
						return concat($zip1,'-',$zip2,'-',$zip3)
				else	$zip 
					)
			else ()
return $out
};
(: -------------------------------------------demo Common Functions-------------------------------------------------------------	  :)
