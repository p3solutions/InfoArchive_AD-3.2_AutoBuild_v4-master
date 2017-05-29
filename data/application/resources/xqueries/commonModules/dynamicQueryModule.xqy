(:
	Author 	: Malik
	Date 	: 15-Apr-2013
	Document: DynamicQueryModule
	Update	: 15-Apr-2016
	Notes	: This module will automatically build the Query statement's where part.
			  The following are the keywords o be used for various comparision operations
				
				=			equal to (with quotes for string)
				!=			not equal to (with quotes for string)
				int=		equal to (without quotes for integers)
				int!=		not equal to (without quotes for integers)
				combined=	list search qith equals
				combined!=	list not equal search
				start		starts with       
				wild		wild card search  
				
:)

module namespace dynamicQueryModule = 'dynamicQueryModule';

import module namespace utility = 'utility' at '/APPLICATIONS/changeme/resources/xqueries/commonModules/utility.xqy';

(: Will omit null input fileds :)
declare function dynamicQueryModule:queryWhereFramer($var as xs:string?, $inputs as item()*, $fields as item()*, $comparisons as item()*) as xs:string?
{
	utility:replace-first
	(
		string-join 
		(
			(
				
				for $inputCounter in 1 to count($inputs)
				where $inputs[$inputCounter] != ''
				return
					let $inputnow	:= $inputs[$inputCounter]
					let $inputNOW	:= replace($inputnow,'&amp;','&amp;amp;')
					let $sym 		:= if ($comparisons[$inputCounter] = ('wild'))
									   then 'wild'
									   else if ($comparisons[$inputCounter] = ('start'))
											then 'start' 
											else if ($comparisons[$inputCounter] = ('combined=','combined!='))
												 then '' 
												 else if ($comparisons[$inputCounter] = ('>', '<', '<=', '>=','int=','int!=') and 
														 string(number($inputNOW)) != 'NaN')
													  then ''
													  else '"'
					let $comparison := if($comparisons[$inputCounter] = ('int=','combined=')) 
									   then '='
									   else if($comparisons[$inputCounter] = ('int!=','combined!=')) 
											then '!='
											else $comparisons[$inputCounter] 
					return
						if ($sym != "wild" and $sym != "start")
						then concat(' and $', $var, '/', $fields[$inputCounter], ' ', $comparison,' ', $sym, $inputNOW, $sym, ' ')	
						else if ($sym = "start")
						then concat(' and starts-with($', $var, '/', $fields[$inputCounter],',"',$inputNOW,'")')
						else (
								let $TextWild   := (: for $x in tokenize($inputNOW, ' ') 
												   return :) 
												   concat($inputNOW,'.*') 
												   (: concat('.*',$inputNOW,'.*') :) (: use this if leading wild card is needed :)
								return
									if ($inputNOW != '')
									then string-join
										(
											(
												for $x in $TextWild
												return
													concat(' and $', $var, '/', $fields[$inputCounter], ' contains text {"',$x, '"} using wildcards ')
											),
											' '
										)
									else ''
							 )
			),
			" "
		), 
		'and', 
		'where'
	)
};


(: Will not omit null input fileds :)
declare function dynamicQueryModule:queryWhereFramerV2($var as xs:string?, $inputs as item()*, $fields as item()*, $comparisons as item()*) as xs:string?
{
	utility:replace-first
	(
		string-join 
		(
			(
				for $inputCounter in 1 to count($inputs)
				return
					let $inputnow	:= $inputs[$inputCounter]
					let $inputNOW	:= replace($inputnow,'&amp;','&amp;amp;')
					let $sym 		:= if ($comparisons[$inputCounter] = ('wild'))
									   then 'wild'
									   else if ($comparisons[$inputCounter] = ('start'))
											then 'start' 
											else if ($comparisons[$inputCounter] = ('combined=','combined!='))
												 then '' 
												 else if ($comparisons[$inputCounter] = ('>', '<', '<=', '>=','int=','int!=') and 
														 string(number($inputNOW)) != 'NaN')
													  then ''
													  else '"'
					let $comparison := if($comparisons[$inputCounter] = ('int=','combined=')) 
									   then '='
									   else if($comparisons[$inputCounter] = ('int!=','combined!=')) 
											then '!='
											else $comparisons[$inputCounter] 
					return
						if ($sym != "wild" and $sym != "start")
						then concat(' and $', $var, '/', $fields[$inputCounter], ' ', $comparison,' ', $sym, $inputNOW, $sym, ' ')	
						else if ($sym = "start")
						then concat(' and starts-with($', $var, '/', $fields[$inputCounter],',"',$inputNOW,'")')
						else (
								let $TextWild   := (: for $x in tokenize($inputNOW, ' ') 
												   return :) 
												   concat($inputNOW,'.*') 
												   (: concat('.*',$inputNOW,'.*') :) (: use this if leading wild card is needed :)
								return
									if ($inputNOW != '')
									then string-join
										(
											(
												for $x in $TextWild
												return
													concat(' and $', $var, '/', $fields[$inputCounter], ' contains text {"',$x, '"} using wildcards ')
											),
											' '
										)
									else ''
							 )
			),
			" "
		), 
		'and', 
		'where'
	)
};