module namespace fv = "fv";


declare function fv:getValues($index)

{
<data><prep>
{doc('/DATA/changeme/Collection/RECONCILIATION')}
</prep>
<dept>
{(doc('/DATA/changeme/Collection/RECONCILIATION')//RECONCILINGDEPT)[$index]}
</dept>
</data>
};