package org.jglrxavpok.thunderscience.common

import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import org.jglrxavpok.thunderscience.ThunderScience

object TemporalChamberRecipes {

    private val recipeResults = hashMapOf<Int, ItemStack>()
    private val recipeCount = hashMapOf<Int, Int>()

    fun registerRecipesFromOreDict() {
        val alreadyRegistered = mutableListOf<String>()
        val oreNames = OreDictionary.getOreNames()
        for(name in oreNames) {
            val predecessor = when {
                name.startsWith("gem") -> "ore${name.substring("gem".length)}"
                name.startsWith("ingot") -> "ore${name.substring("ingot".length)}"
                name.startsWith("nugget") -> "ingot${name.substring("nugget".length)}"
                else -> null
            }
            val requiredCount = when {
                name.startsWith("nugget") -> 9
                else -> 1
            }
            if(predecessor != null) {
                if(OreDictionary.doesOreNameExist(predecessor)) {
                    if(predecessor !in alreadyRegistered) {
                        registerTemporalRecipe(name, requiredCount, predecessor)
                        alreadyRegistered += predecessor
                    }
                }
            }
        }
    }

    fun registerTemporalRecipe(itemOreName: String, itemCount: Int, resultOreName: String) {
        val inputs = OreDictionary.getOres(itemOreName)
        val results = OreDictionary.getOres(resultOreName)
        if(results.isEmpty()) {
            ThunderScience.logger.info("Tried to register temporal recipe from ore names ($itemOreName x$itemCount -> $resultOreName) but no stack was found")
            return
        }
        val result = results[0]

        for(input in inputs) {
            val inputStack = input.copy()
            inputStack.count = itemCount
            registerTemporalRecipe(inputStack, result)
        }
    }

    /**
     * Does not check count
     */
    fun isRecipeInput(stack: ItemStack): Boolean {
        return getHash(stack) in recipeResults
    }

    fun getCorrespondingResult(stack: ItemStack): TemporalRecipe {
        val hash = getHash(stack)
        if(!isRecipeInput(stack))
            return InvalidRecipe
        val count = recipeCount[hash]!!
        if(count > stack.count)
            return InvalidRecipe
        return TemporalRecipe(recipeResults[hash]!!, count)
    }

    fun registerTemporalRecipe(input: ItemStack, result: ItemStack) {
        if(isRecipeInput(input)) {
            ThunderScience.logger.warn("Tried to register conflicting temporal recipe ${input.item.registryName} -> ${result.item.registryName}")
        } else {
            val hash = getHash(input)
            recipeResults[hash] = result
            recipeCount[hash] = input.count
            ThunderScience.logger.info("Registered temporal recipe ($input -> $result)")
        }
    }

    private fun getHash(stack: ItemStack): Int {
        var hash = stack.item.registryName.toString().hashCode()
        hash *= 31
        hash += stack.itemDamage
        return hash
    }
}

data class TemporalRecipe(val output: ItemStack, val requiredInputCount: Int)
val InvalidRecipe = TemporalRecipe(ItemStack.EMPTY, 0)
