import org.spectral.asm.core.ClassPool
import org.spectral.asm.simulator.MethodSimulator
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File

object Test : Spek({
    describe("ldsfjsldf") {
        it("fsljf") {
            val pool = ClassPool()
            pool.addArchive(File("C:\\Users\\Kyle\\Projects\\Spectral\\workspaces\\spectral-powered\\asm\\gamepack-deob-188.jar"))
            pool.init()

            val method = pool["client"]!!.getMethod("init", "()V")!!
            val analyzer = MethodSimulator(method)
            analyzer.analyze(method.owner.name, method.node)
        }
    }
})