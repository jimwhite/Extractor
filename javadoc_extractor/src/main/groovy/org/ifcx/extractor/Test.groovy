package org.ifcx.extractor

a = new BigInteger[2][2]
a[0][0] = 1
a[0][1] = 1
a[1][0] = 1
a[1][1] = 0
temp = new BigInteger[2][2]
temp = a
def testFun(def a,BigInteger n) {
    while (n-- > 1) {
        def b = new BigInteger[2][2]
        def sum = 0
        for ( c = 0 ; c < 2 ; c++) {
            for ( d = 0 ; d < 2 ; d++ )
            {
                for ( k = 0 ; k < 2 ; k++ )
                {
                    sum = sum + temp[c][k] * a[k][d];
                }
                b[c][d] = sum
                sum = 0;
            }
        }
        a = b
    }
    a
}
c = new BigInteger[2][2]
BigInteger n = 15090
if( n % 2 == 0 )
{
    c =  testFun(a,n.divide(2))
    def temp = c
    c = testFun(c,2)
}
println c[0][1]
