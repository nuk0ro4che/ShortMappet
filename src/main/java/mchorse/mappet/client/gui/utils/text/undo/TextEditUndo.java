package mchorse.mappet.client.gui.utils.text.undo;

import mchorse.mappet.client.gui.utils.text.GuiMultiTextElement;
import mchorse.mappet.client.gui.utils.text.utils.Cursor;
import mchorse.mclib.utils.undo.IUndo;

public class TextEditUndo implements IUndo<GuiMultiTextElement> {
   public String text;
   public Cursor cursor;
   public Cursor selection;
   public String postText;
   public Cursor postCursor;
   public Cursor postSelection;
   public boolean ready;

   public TextEditUndo(GuiMultiTextElement element) {
      this(element.getSelectedText(), element.cursor, element.selection);
   }

   public TextEditUndo(String text, Cursor cursor, Cursor selection) {
      this.cursor = new Cursor(-1, 0);
      this.selection = new Cursor(-1, 0);
      this.postText = "";
      this.postCursor = new Cursor(-1, 0);
      this.postSelection = new Cursor(-1, 0);
      this.text = text;
      this.cursor.copy(cursor);
      this.selection.copy(selection);
   }

   public void post(String postText, Cursor postCursor, Cursor postSelection) {
      this.postText = postText;
      this.postCursor.copy(postCursor);
      this.postSelection.copy(postSelection);
   }

   public TextEditUndo ready() {
      this.ready = true;
      return this;
   }

   public IUndo<GuiMultiTextElement> noMerging() {
      return this;
   }

   public boolean isMergeable(IUndo<GuiMultiTextElement> undo) {
      if (undo instanceof TextEditUndo text) {
         if (this.getType() == text.getType() && text.getType() != TextEditUndo.UndoType.REPLACE) {
            if (this.postCursor.line != text.postCursor.line) {
               return false;
            }

            if (this.getType() == TextEditUndo.UndoType.INSERT && text.cursor.offset != this.cursor.offset + this.postText.length()) {
               return false;
            }

            if (this.getType() == TextEditUndo.UndoType.DELETE) {
               if (this.isBackspace() && text.isBackspace()) {
                  if (!this.cursor.isEqualTo(text.cursor)) {
                     return false;
                  }
               } else if (text.cursor.offset != this.cursor.offset - this.text.length()) {
                  return false;
               }
            }

            return this.isBackspace() == text.isBackspace() && !text.wasSelecting() && !this.wasSelecting();
         }
      }

      return false;
   }

   public void merge(IUndo<GuiMultiTextElement> undo) {
      TextEditUndo text = (TextEditUndo)undo;
      if (text.getType() == TextEditUndo.UndoType.INSERT) {
         this.mergeInsert(text);
      } else if (text.getType() == TextEditUndo.UndoType.DELETE) {
         this.mergeDelete(text);
      }

   }

   private void mergeInsert(TextEditUndo text) {
      this.postCursor.copy(text.postCursor);
      this.postSelection.copy(text.postSelection);
      this.postText = this.postText + text.postText;
   }

   private void mergeDelete(TextEditUndo text) {
      if (this.isBackspace()) {
         this.postCursor.copy(text.postCursor);
         this.postSelection.copy(text.postSelection);
         this.text = this.text + text.text;
      } else {
         this.postCursor.copy(text.postCursor);
         this.postSelection.copy(text.postSelection);
         this.text = text.text + this.text;
      }

   }

   public void undo(GuiMultiTextElement element) {
      element.cursor.copy(this.cursor);
      element.selection.copy(this.selection);
      UndoType type = this.getType();
      if (type == TextEditUndo.UndoType.REPLACE || type == TextEditUndo.UndoType.INSERT) {
         if (element.selection.isThisLessTo(element.cursor)) {
            element.swapSelection();
         }

         element.selectTextful(this.postText, false);
         element.deleteSelection();
      }

      if (type == TextEditUndo.UndoType.REPLACE || type == TextEditUndo.UndoType.DELETE) {
         if (!this.wasSelecting() || type == TextEditUndo.UndoType.DELETE) {
            element.cursor.copy(this.postCursor);
            element.selection.copy(this.postSelection);
         }

         element.writeString(this.text);
      }

      element.cursor.copy(this.cursor);
      element.selection.copy(this.selection);
   }

   public void redo(GuiMultiTextElement element) {
      element.cursor.copy(this.cursor);
      element.selection.copy(this.selection);
      UndoType type = this.getType();
      if (type == TextEditUndo.UndoType.REPLACE || type == TextEditUndo.UndoType.DELETE) {
         if (element.cursor.isThisLessTo(element.selection)) {
            element.swapSelection();
         }

         if (element.isSelected()) {
            element.deleteSelection();
         } else {
            boolean backspace = this.isBackspace();

            for(int i = 0; i < this.text.length(); ++i) {
               if (backspace) {
                  element.moveCursor(1, 0);
               }

               element.deleteCharacter();
            }
         }
      }

      if (type == TextEditUndo.UndoType.REPLACE || type == TextEditUndo.UndoType.INSERT) {
         element.writeString(this.postText);
      }

      element.cursor.copy(this.postCursor);
      element.selection.copy(this.postSelection);
   }

   public boolean isBackspace() {
      return this.getType() == TextEditUndo.UndoType.DELETE && this.cursor.isEqualTo(this.postCursor);
   }

   public UndoType getType() {
      if (!this.text.isEmpty() && this.postText.isEmpty()) {
         return TextEditUndo.UndoType.DELETE;
      } else {
         return this.text.isEmpty() && !this.postText.isEmpty() ? TextEditUndo.UndoType.INSERT : TextEditUndo.UndoType.REPLACE;
      }
   }

   public boolean wasSelecting() {
      return !this.selection.isEmpty();
   }

   public static enum UndoType {
      REPLACE,
      DELETE,
      INSERT;
      private static UndoType[] $values() {
         return new UndoType[]{REPLACE, DELETE, INSERT};
      }
   }
}
