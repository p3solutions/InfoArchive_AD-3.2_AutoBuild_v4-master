module namespace mask = 'mask';

(:
declare function mask:inList($items as xs:string*, $target as item()) as xs:boolean {
    let $val := fn:exists(fn:index-of($items, string($target)))
    return $val
};
:)

declare function mask:isRestricted($restrictions as xs:string, $targetField as xs:string) as xs:boolean {
    let $tokens := tokenize($restrictions, '[ ,]+')
    return exists(index-of($tokens, $targetField))
};

(: 
    Returns a string similar to $raw, with digits replaced with 'x', keeping a tail portion visible.
    For example, mask:maskLeadingNumerals('AB123-45-6789', 4) -> 'ABxxx-xx-6789' 
:)
declare function mask:maskLeadingNumerals($raw as xs:string, $visibleTail as xs:integer) as xs:string {
    let $len := string-length($raw)
    let $prefix := substring($raw, 1, $len - $visibleTail)
    let $suffix := substring($raw, 1 + $len - $visibleTail)
    return concat(translate($prefix, '0123456789', 'xxxxxxxxxx'), $suffix) 
};

declare function mask:maskLeadingNumerals($restrictions as xs:string, $targetField as xs:string, $raw as xs:string, $visibleTail as xs:integer) as xs:string {
    let $isRestricted := mask:isRestricted($restrictions, $targetField)
    return if($isRestricted) then mask:maskLeadingNumerals($raw, $visibleTail) else $raw
};