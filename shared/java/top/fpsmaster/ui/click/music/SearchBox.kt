package top.fpsmaster.ui.click.music

import com.google.common.base.Predicate
import com.google.common.base.Predicates
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.ChatAllowedCharacters
import top.fpsmaster.FPSMaster
import top.fpsmaster.font.impl.UFontRenderer
import top.fpsmaster.utils.math.MathUtils.clamp
import top.fpsmaster.utils.math.animation.ColorAnimation
import top.fpsmaster.utils.render.Render2DUtils
import java.awt.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SearchBox : Gui {
    val font: UFontRenderer?

    /**
     * The width of this text field.
     */
    var width: Float = 0f
    var height: Float = 0f
    private var xPosition: Float = 0f
    private var yPosition: Float = 0f

    /**
     * Has the current text being edited on the textbox.
     */
    var text: String = ""
        set(textIn) {
            if (validator.apply(textIn)) {
                field = if (textIn.length > this.maxStringLength) {
                    textIn.substring(0, this.maxStringLength)
                } else {
                    textIn
                }

                this.setCursorPositionEnd()
            }
        }
    private var maxStringLength = 1000
        set(length: Int) {
            field = length

            if (text.length > length) {
                this.text = text.substring(0, length)
            }
        }
    private var cursorCounter: Int = 0
    private var enableBackgroundDrawing: Boolean = true
    private var canLoseFocus: Boolean = true
    private var isFocused: Boolean = false
        set(isFocusedIn) {
            if (isFocusedIn && !this.isFocused) {
                this.cursorCounter = 0
            }

            field = isFocusedIn
        }
    private var isEnabled = true
    private var lineScrollOffset: Int = 0
    private var cursorPosition: Int = 0
        set(pos) {
            field = pos
            val i = text.length
            field = clamp(this.cursorPosition, 0, i)
            this.setSelectionPos(this.cursorPosition)
        }

    private var selectionEnd: Int = 0
    private var enabledColor: Color
    private val disabledColor: Color

    private val validator: Predicate<String> = Predicates.alwaysTrue()

    private var placeholder: String = ""
    private var btnColor: ColorAnimation = ColorAnimation()

    var visible: Boolean = true
    var runnable: Runnable? = null

    constructor(
        placehoder: String,
        enable: Color,
        disable: Color,
        focus: Color?,
        hover: Color?,
        fontrendererObj: UFontRenderer?
    ) {
        this.placeholder = placehoder
        this.enabledColor = enable
        this.disabledColor = disable
        this.font = fontrendererObj
    }

    constructor(placehoder: String, fontrendererObj: UFontRenderer?, runnable: Runnable?) {
        this.font = fontrendererObj
        this.runnable = runnable
        this.placeholder = placehoder
        this.enabledColor = FPSMaster.theme.textboxEnabled
        this.disabledColor = FPSMaster.theme.textboxDisabled
    }

    constructor(placehoder: String) {
        this.font = FPSMaster.fontManager.s18
        this.placeholder = placehoder
        this.enabledColor = FPSMaster.theme.textboxEnabled
        this.disabledColor = FPSMaster.theme.textboxDisabled
    }

    constructor(s: String, runnable: Runnable?) {
        this.font = FPSMaster.fontManager.s18
        this.placeholder = s
        this.runnable = runnable
        this.enabledColor = FPSMaster.theme.textboxEnabled
        this.disabledColor = FPSMaster.theme.textboxDisabled
    }

    /**
     * Increments the cursor counter
     */
    fun updateCursorCounter() {
        ++this.cursorCounter
    }

    var content: String
        /**
         * Returns the contents of the textbox
         */
        get() = this.text
        set(content) {
            this.text = content
            this.setCursorPositionEnd()
        }

    private val selectedText: String
        /**
         * returns the text between the cursor and selectionEnd
         */
        get() {
            val i = min(cursorPosition.toDouble(), selectionEnd.toDouble())
                .toInt()
            val j = max(cursorPosition.toDouble(), selectionEnd.toDouble())
                .toInt()
            return text.substring(i, j)
        }

    /**
     * Adds the given text after the cursor, or replaces the currently selected text if there is a selection.
     */
    private fun writeText(textToWrite: String?) {
        var s = ""
        val s1 = ChatAllowedCharacters.filterAllowedCharacters(textToWrite)
        val i = min(cursorPosition.toDouble(), selectionEnd.toDouble())
            .toInt()
        val j = max(cursorPosition.toDouble(), selectionEnd.toDouble())
            .toInt()
        val k = this.maxStringLength - text.length - (i - j)

        if (text.isNotEmpty()) {
            s += text.substring(0, i)
        }

        val l: Int

        if (k < s1.length) {
            s += s1.substring(0, k)
            l = k
        } else {
            s += s1
            l = s1.length
        }

        if (text.isNotEmpty() && j < text.length) {
            s += text.substring(j)
        }

        if (validator.apply(s)) {
            this.text = s
            this.moveCursorBy(i - this.selectionEnd + l)
        }
    }

    /**
     * Deletes the given number of words from the current cursor's position, unless there is currently a selection, in
     * which case the selection is deleted instead.
     */
    private fun deleteWords(num: Int) {
        if (text.isNotEmpty()) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("")
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition)
            }
        }
    }

    /**
     * Deletes the given number of characters from the current cursor's position, unless there is currently a selection,
     * in which case the selection is deleted instead.
     */
    private fun deleteFromCursor(num: Int) {
        if (text.isNotEmpty()) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("")
            } else {
                val flag = num < 0
                val i = if (flag) this.cursorPosition + num else this.cursorPosition
                val j = if (flag) this.cursorPosition else this.cursorPosition + num
                var s = ""

                if (i >= 0) {
                    s = text.substring(0, i)
                }

                if (j < text.length) {
                    s += text.substring(j)
                }

                if (validator.apply(s)) {
                    this.text = s

                    if (flag) {
                        this.moveCursorBy(num)
                    }
                }
            }
        }
    }

    /**
     * Gets the starting index of the word at the specified number of words away from the cursor position.
     */
    private fun getNthWordFromCursor(numWords: Int): Int {
        return this.getNthWordFromPos(numWords, this.cursorPosition)
    }

    /**
     * Gets the starting index of the word at a distance of the specified number of words away from the given position.
     */
    private fun getNthWordFromPos(n: Int, pos: Int): Int {
        return this.getNthWordFromPosWS(n, pos, true)
    }

    /**
     * Like getNthWordFromPos (which wraps this), but adds option for skipping consecutive spaces
     */
    private fun getNthWordFromPosWS(n: Int, pos: Int, skipWs: Boolean): Int {
        var i = pos
        val flag = n < 0
        val j = abs(n.toDouble()).toInt()

        for (k in 0 until j) {
            if (!flag) {
                val l = text.length
                i = text.indexOf(32.toChar(), i)

                if (i == -1) {
                    i = l
                } else {
                    while (skipWs && i < l && text[i] == ' ') {
                        ++i
                    }
                }
            } else {
                while ((skipWs && i > 0) && text[i - 1] == ' ') {
                    --i
                }

                while (i > 0 && text[i - 1] != ' ') {
                    --i
                }
            }
        }

        return i
    }

    /**
     * Moves the text cursor by a specified number of characters and clears the selection
     */
    private fun moveCursorBy(num: Int) {
        this.cursorPosition = this.selectionEnd + num
    }

    /**
     * Moves the cursor to the very start of this text box.
     */
    private fun setCursorPositionZero() {
        this.cursorPosition = 0
    }

    /**
     * Moves the cursor to the very end of this text box.
     */
    private fun setCursorPositionEnd() {
        this.cursorPosition = text.length
    }

    /**
     * Call this method from your GuiScreen to process the keys into the textbox
     */
    fun keyTyped(typedChar: Char, keyCode: Int): Boolean {
        if (!this.isFocused) {
            return false
        } else if (GuiScreen.isKeyComboCtrlA(keyCode)) {
            this.setCursorPositionEnd()
            this.setSelectionPos(0)
            return true
        } else if (GuiScreen.isKeyComboCtrlC(keyCode)) {
            GuiScreen.setClipboardString(this.selectedText)
            return true
        } else if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            if (this.isEnabled) {
                this.writeText(GuiScreen.getClipboardString())
            }

            return true
        } else if (GuiScreen.isKeyComboCtrlX(keyCode)) {
            GuiScreen.setClipboardString(this.selectedText)

            if (this.isEnabled) {
                this.writeText("")
            }

            return true
        } else {
            when (keyCode) {
                14 -> {
                    if (GuiScreen.isCtrlKeyDown()) {
                        if (this.isEnabled) {
                            this.deleteWords(-1)
                        }
                    } else if (this.isEnabled) {
                        this.deleteFromCursor(-1)
                    }

                    return true
                }

                199 -> {
                    if (GuiScreen.isShiftKeyDown()) {
                        this.setSelectionPos(0)
                    } else {
                        this.setCursorPositionZero()
                    }

                    return true
                }

                203 -> {
                    if (GuiScreen.isShiftKeyDown()) {
                        if (GuiScreen.isCtrlKeyDown()) {
                            this.setSelectionPos(
                                this.getNthWordFromPos(
                                    -1,
                                    selectionEnd
                                )
                            )
                        } else {
                            this.setSelectionPos(this.selectionEnd - 1)
                        }
                    } else if (GuiScreen.isCtrlKeyDown()) {
                        this.cursorPosition = this.getNthWordFromCursor(-1)
                    } else {
                        this.moveCursorBy(-1)
                    }

                    return true
                }

                205 -> {
                    if (GuiScreen.isShiftKeyDown()) {
                        if (GuiScreen.isCtrlKeyDown()) {
                            this.setSelectionPos(
                                this.getNthWordFromPos(
                                    1,
                                    selectionEnd
                                )
                            )
                        } else {
                            this.setSelectionPos(this.selectionEnd + 1)
                        }
                    } else if (GuiScreen.isCtrlKeyDown()) {
                        this.cursorPosition = this.getNthWordFromCursor(1)
                    } else {
                        this.moveCursorBy(1)
                    }

                    return true
                }

                207 -> {
                    if (GuiScreen.isShiftKeyDown()) {
                        this.setSelectionPos(text.length)
                    } else {
                        this.setCursorPositionEnd()
                    }

                    return true
                }

                211 -> {
                    if (GuiScreen.isCtrlKeyDown()) {
                        if (this.isEnabled) {
                            this.deleteWords(1)
                        }
                    } else if (this.isEnabled) {
                        this.deleteFromCursor(1)
                    }
                    return true
                }

                28 -> {
                    if (runnable != null) {
                        runnable!!.run()
                    }
                    return true
                }

                else -> if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                    if (this.isEnabled) {
                        this.writeText(typedChar.toString())
                    }

                    return true
                } else {
                    return false
                }
            }
        }
    }

    /**
     * Called when mouse is clicked, regardless as to whether it is over this button or not.
     */
    fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Boolean {
        val flag =
            (mouseX >= this.xPosition && mouseX < xPosition + this.width && mouseY >= yPosition) && mouseY < this.yPosition + this.height

        if (this.canLoseFocus) {
            this.isFocused = flag
        }

        if (this.isFocused && flag && (mouseButton == 0)) {
            var i = mouseX - this.xPosition

            if (this.enableBackgroundDrawing) {
                i -= 4f
            }

            val s = font!!.trimStringToWidth(text.substring(this.lineScrollOffset), this.getWidth())
            this.cursorPosition = font.trimStringToWidth(s, i.toInt()).length + this.lineScrollOffset
            return true
        } else {
            return false
        }
    }

    /**
     * Draws the textbox
     */
    fun render(x: Float, y: Float, width: Float, height: Float, mouseX: Int, mouseY: Int) {
        this.xPosition = x
        this.yPosition = y
        this.width = width
        this.height = height
        if (this.visible) {
            if (Render2DUtils.isHovered(x, y, width, height, mouseX, mouseY)) {
                if (isFocused) {
                    btnColor.base(FPSMaster.theme.textboxFocus)
                } else {
                    btnColor.base(FPSMaster.theme.textboxHover)
                }
            } else {
                btnColor.base(enabledColor)
            }
            Render2DUtils.drawOptimizedRoundedRect(xPosition, yPosition, width, height, btnColor.color)

            val j = this.cursorPosition - this.lineScrollOffset
            var k = this.selectionEnd - this.lineScrollOffset
            val s = font!!.trimStringToWidth(text.substring(this.lineScrollOffset), this.getWidth())
            val flag = j >= 0 && j <= s.length
            val flag1 = this.isFocused && (this.cursorCounter / 6 % 2 == 0) && flag
            val l = if (this.enableBackgroundDrawing) this.xPosition + 4 else this.xPosition
            val i1 = if (this.enableBackgroundDrawing) this.yPosition + (this.height - 8) / 2 else this.yPosition
            var j1 = l

            if (k > s.length) {
                k = s.length
            }

            if (s.isEmpty() && !isFocused) {
                font.drawStringWithShadow(placeholder, xPosition + 4, i1, -1)
            }

            val s1 = if (flag) s.substring(0, j) else s
            j1 = font.drawStringWithShadow(s1, l + 2, i1, -1).toFloat()


            val flag2 = this.cursorPosition < text.length || text.length >= this.getMaxStringLength()
            var k1 = j1

            if (!flag) {
                k1 = if (j > 0) l + this.width else l
            } else if (flag2) {
                k1 = j1 - 1
                --j1
            }

            if (s.isNotEmpty() && flag && j < s.length) {
                font.drawStringWithShadow(s.substring(j), xPosition + j1 + 8, i1, -1)
            }

            if (flag1) {
                if (flag2) {
                    Render2DUtils.drawRect(xPosition + j1 + 8, i1 - 1, 1f, (1 + font.height).toFloat(), -3092272)
                } else {
                    font.drawStringWithShadow("_", xPosition + j1 + 8, i1, -1)
                }
            }

            if (k != j) {
                val l1 = l + font.getStringWidth(s.substring(0, k))
                this.drawCursorVertical(xPosition + k1 + 8, i1 - 1, l1 + 2, i1 + 1 + font.height)
            }
        }
    }

    /**
     * Draws the current selection and a vertical line cursor in the text box.
     */
    private fun drawCursorVertical(startX: Float, startY: Float, endX: Float, endY: Float) {
        var startX = startX
        var startY = startY
        var endX = endX
        var endY = endY
        if (startX < endX) {
            val i = startX
            startX = endX
            endX = i
        }

        if (startY < endY) {
            val j = startY
            startY = endY
            endY = j
        }

        if (endX > this.xPosition + this.width) {
            endX = this.xPosition + this.width
        }

        if (startX > this.xPosition + this.width) {
            startX = this.xPosition + this.width
        }

        Render2DUtils.drawRect(startX, startY, endX - startX, endY - startY, Color(255, 255, 255, 100))
    }

    /**
     * returns the maximum number of character that can be contained in this textbox
     */
    private fun getMaxStringLength(): Int {
        return this.maxStringLength
    }


    /**
     * returns the width of the textbox depending on if background drawing is enabled
     */
    private fun getWidth(): Int {
        return (if (this.enableBackgroundDrawing) this.width - 8 else this.width).toInt()
    }

    /**
     * Sets the position of the selection anchor (the selection anchor and the cursor position mark the edges of the
     * selection). If the anchor is set beyond the bounds of the current text, it will be put back inside.
     */
    private fun setSelectionPos(position: Int) {
        var position = position
        val i = text.length

        if (position > i) {
            position = i
        }

        if (position < 0) {
            position = 0
        }

        this.selectionEnd = position

        if (this.font != null) {
            if (this.lineScrollOffset > i) {
                this.lineScrollOffset = i
            }

            val j = getWidth().toFloat()
            val s =
                font.trimStringToWidth(text.substring(this.lineScrollOffset), j.toInt())
            val k = s.length + this.lineScrollOffset

            if (position == this.lineScrollOffset) {
                this.lineScrollOffset -= font.trimStringToWidth(this.text, j.toInt(), true).length
            }

            if (position > k) {
                this.lineScrollOffset += position - k
            } else if (position <= this.lineScrollOffset) {
                this.lineScrollOffset -= this.lineScrollOffset - position
            }

            this.lineScrollOffset = clamp(this.lineScrollOffset, 0, i)
        }
    }
}
