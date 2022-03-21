print "How many: "; 
read count; 
 
var first: int := 0; 
var second: int := 1; 
var y: int; 
 
print first; 
print "%n"; 
for x in 0..count-1 do 
    print second; 
    print "%n"; 
    y := second; 
    second := second + first; 
    first := y; 
end for; 
 
assert (first = second);