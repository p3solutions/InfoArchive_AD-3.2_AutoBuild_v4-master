module namespace common = 'common';

(: build where clause components from partial strings :)
declare function common:addClause($whereClause as xs:string, $var as xs:string*, $expr as xs:string) as xs:string {
    if (empty($var) or $var = '' or normalize-space($var) = '') then $whereClause
    else if ($whereClause = "") then $expr
    else concat($whereClause, " and ", $expr)
};

(: build range where clause component :)
declare function common:addRangeClause(
    $whereClause as xs:string, $fromStr as xs:string, $toStr as xs:string, $elementName as xs:string) as xs:string {

    if (empty($fromStr) or empty($toStr) or ($fromStr = '') or ($toStr = '')
        or normalize-space($fromStr) = '' or normalize-space($toStr) = '')
    then $whereClause
    else
        if ($whereClause = '')
        then concat($elementName, "[. >= '", $fromStr, "' and . <= '", $toStr, "'] ")
        else concat($whereClause, " and ", $elementName, "[. >= '", $fromStr, "' and . <= '", $toStr, "'] ")
};

(: convert dates like yyyymmddhhmmss or yyyymmddhhmm or yyyymmdd to yyyy-mm-dd hh:mm:ss style format :)
declare function common:date14readable($dateString as xs:string) as xs:string {

    (: assume we always have at least yyyymmdd :)
    let $result := concat(substring($dateString, 1, 4), '-', substring($dateString, 5, 2), '-', substring($dateString, 7, 2))

    let $hour := substring($dateString, 9, 2)
    let $min := substring($dateString, 11, 2)
    (: assume we don't have yyyymmddhh - if hours exist, will have minutes :)
    let $result := if (empty($hour) or ($hour = ''))
        then $result
        else concat($result, ' ', $hour, ':', $min)

    let $sec := substring($dateString, 13, 2)
    let $result := if (empty($sec) or ($sec = ''))
        then $result
        else concat($result, ':', $sec)
    return $result
};

(: get a count based subsequence from a provided query :)
declare function common:getQuerySubsequence($query as item()*, $first as xs:string, $last as xs:string) as element()? {

    let $count := count($query)
    let $listcount := if ($last = '-1')
        then ($count + 1)
        else ($last cast as xs:integer - $first cast as xs:integer) + 1
    return  <results total='{ $count }'> {
        for $elem in subsequence($query, $first cast as xs:integer, $listcount) return $elem
    } </results>
};
