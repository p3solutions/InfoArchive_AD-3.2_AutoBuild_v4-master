module namespace functx = 'functx';

(: return maximum value of a collection of values, treating each value as a string :)
declare function functx:max-string
    ( $strings as xs:anyAtomicType* )  as xs:string? {

     max(for $string in $strings return string($string))
 };


(: add attributes to an element :)
declare function functx:add-or-update-attributes
  ( $elements as element()* ,
    $attrNames as xs:QName* ,
    $attrValues as xs:anyAtomicType* )  as element()? {

   for $element in $elements
   return element { node-name($element)}
                  { $element/@*[not(node-name(.) = $attrNames)],
                    for $attrName at $seq in $attrNames
                        return attribute {$attrName} {$attrValues[$seq]},
                    $element/node() }
};
